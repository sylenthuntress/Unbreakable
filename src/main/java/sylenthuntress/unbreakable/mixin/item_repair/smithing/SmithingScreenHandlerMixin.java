package sylenthuntress.unbreakable.mixin.item_repair.smithing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.access.SmithingScreenHandlerAccess;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.RepairHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.function.Predicate;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler implements SmithingScreenHandlerAccess {
    @Shadow
    @Final
    private RecipePropertySet basePropertySet;
    @Shadow
    @Final
    private RecipePropertySet additionPropertySet;
    @Unique
    private Property repairMaterialCost = Property.create();
    @Unique
    int scaledWithShatterLevel = -1;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairMaterials(Args args) {
        if (Unbreakable.CONFIG.smithingRepair.ALLOW()) {
            Predicate<ItemStack> originalPredicate = args.get(3);
            args.set(3, originalPredicate.or((stack) -> RepairHelper.getRegistryInstance().isRepairMaterial(stack.getItem())));
        }
    }

    @Unique
    private void setRepairMaterialCost(int cost) {
        repairMaterialCost.set(cost);
    }

    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairableItemsAsBase(Args args) {
        if (Unbreakable.CONFIG.smithingRepair.ALLOW()) {
            Predicate<ItemStack> originalPredicate = args.get(3);
            args.set(3, originalPredicate.or(ItemStack::isDamageable));
        }
    }

    @Unique
    public int unbreakable$getRepairCost() {
        return this.repairMaterialCost.get();
    }

    @WrapOperation(method = "isValidIngredient", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipePropertySet;canUse(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean allowQuickMove(RecipePropertySet instance, ItemStack stack, Operation<Boolean> original) {
        boolean isValidForRepairs = false;
        if (instance == basePropertySet) {
            isValidForRepairs = stack.isDamageable();
        } else if (instance == additionPropertySet)
            isValidForRepairs = RepairHelper.getRegistryInstance().isRepairMaterial(stack);
        return original.call(instance, stack) || isValidForRepairs;
    }

    @Inject(method = "method_64654", at = @At("TAIL"))
    private void repairItemLogic(CallbackInfo ci) {
        repairMaterialCost.set(0);
        ItemStack repairBase = this.getSlot(1).getStack();
        ItemStack repairMaterial = this.getSlot(2).getStack();
        ItemStack outputStack = repairBase.copy();
        if (Unbreakable.CONFIG.smithingRepair.ALLOW() && repairBase.isDamaged() && repairBase.canRepairWith(repairMaterial)) {
            int repairFactor = RepairHelper.calculateRepairFactor(
                    4,
                    outputStack,
                    repairBase,
                    scaledWithShatterLevel == outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0),
                    RepairHelper.RepairStations.SMITHING_TABLE
            );
            scaledWithShatterLevel = outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            int materialCost = 0;
            while (repairFactor > 0 && materialCost < repairMaterial.getCount()) {
                outputStack.setDamage(outputStack.getDamage() - repairFactor);
                if (Unbreakable.CONFIG.smithingRepair.COST.DEGRADE_REPAIR_FACTOR())
                    outputStack.set(ModComponents.SMITHING_DEGRADATION, Math.min(20, outputStack.getOrDefault(ModComponents.SMITHING_DEGRADATION, 0) + 1));
                if (Unbreakable.CONFIG.grindingRepair.COST.SMITHING_DECREMENTS_DEGRADATION())
                    outputStack.set(ModComponents.GRINDING_DEGRADATION, Math.max(0, outputStack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0) - 2));
                repairFactor = RepairHelper.calculateRepairFactor(
                        4,
                        outputStack,
                        repairBase,
                        scaledWithShatterLevel == outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0),
                        RepairHelper.RepairStations.SMITHING_TABLE
                );
                scaledWithShatterLevel = outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
                materialCost++;
            }
            setRepairMaterialCost(materialCost);
            this.getSlot(3).setStack(outputStack);
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void decrementRepairMaterials(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (unbreakable$getRepairCost() > 0) {
            if (!player.isCreative())
                this.getSlot(1).getStack().decrement(1);
            this.getSlot(2).getStack().decrement(unbreakable$getRepairCost());
            setRepairMaterialCost(0);
            this.quickMove(player, 2);
            ci.cancel();
        }
    }
}