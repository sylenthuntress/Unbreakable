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
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.access.SmithingScreenHandlerAccess;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.RepairHelper;

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
    private final Property unbreakable$repairMaterialCost = Property.create();
    @Unique
    private int unbreakable$scaledWithShatterLevel = -1;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @ModifyArgs(
            method = "createForgingSlotsManager",
            at = @At(
                    value = "INVOKE",
                    ordinal = 2,
                    target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"
            )
    )
    private static void unbreakable$allowRepairMaterials(Args args) {
        if (!Unbreakable.CONFIG.smithingRepair.ALLOW()) {
            return;
        }

        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or(RepairHelper::isRepairMaterial));
    }

    @ModifyArgs(
            method = "createForgingSlotsManager",
            at = @At(
                    value = "INVOKE",
                    ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"
            )
    )
    private static void unbreakable$allowRepairableItemsAsBase(Args args) {
        if (!Unbreakable.CONFIG.smithingRepair.ALLOW()) {
            return;
        }

        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or(ItemStack::isDamageable));
    }

    @Unique
    private void unbreakable$setRepairMaterialCost(int cost) {
        unbreakable$repairMaterialCost.set(cost);
    }

    @Unique
    public int unbreakable$getRepairCost() {
        return this.unbreakable$repairMaterialCost.get();
    }

    @WrapOperation(
            method = "isValidIngredient",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/recipe/RecipePropertySet;canUse(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean unbreakable$allowQuickMove(RecipePropertySet instance, ItemStack stack, Operation<Boolean> original) {
        boolean isValidForRepairs = false;

        if (instance == basePropertySet) {
            isValidForRepairs = stack.isDamageable();
        } else if (instance == additionPropertySet) {
            isValidForRepairs = RepairHelper.isRepairMaterial(stack);
        }

        return original.call(instance, stack) || isValidForRepairs;
    }

    @Inject(
            method = "method_64654",
            at = @At("TAIL")
    )
    private void unbreakable$doRepairItemLogic(CallbackInfo ci) {
        unbreakable$repairMaterialCost.set(0);
        final ItemStack repairBase = this.getSlot(1).getStack();
        final ItemStack repairMaterial = this.getSlot(2).getStack();

        if (!Unbreakable.CONFIG.smithingRepair.ALLOW()
                || !repairBase.isDamaged()
                || !repairBase.canRepairWith(repairMaterial)) {
            return;
        }

        ItemStack outputStack = repairBase.copy();

        int repairFactor = RepairHelper.calculateRepairFactor(
                4,
                outputStack,
                repairBase,
                unbreakable$scaledWithShatterLevel == outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0),
                RepairHelper.RepairStations.SMITHING_TABLE
        );
        unbreakable$scaledWithShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int materialCost = 0;

        // Repair output item
        while (repairFactor > 0 && materialCost < repairMaterial.getCount()) {
            outputStack.setDamage(outputStack.getDamage() - repairFactor);

            if (Unbreakable.CONFIG.smithingRepair.COST.DEGRADE_REPAIR_FACTOR()) {
                outputStack.set(
                        UnbreakableComponents.SMITHING_DEGRADATION,
                        Math.min(20,
                                outputStack.getOrDefault(
                                        UnbreakableComponents.SMITHING_DEGRADATION,
                                        0
                                ) + 1
                        )
                );
            }
            if (Unbreakable.CONFIG.grindingRepair.COST.SMITHING_DECREMENTS_DEGRADATION()) {
                outputStack.set(
                        UnbreakableComponents.GRINDING_DEGRADATION,
                        Math.max(0,
                                outputStack.getOrDefault(
                                        UnbreakableComponents.GRINDING_DEGRADATION,
                                        0
                                ) - 2
                        )
                );
            }

            repairFactor = RepairHelper.calculateRepairFactor(
                    4,
                    outputStack,
                    repairBase,
                    unbreakable$scaledWithShatterLevel == outputStack.getOrDefault(
                            UnbreakableComponents.SHATTER_LEVEL,
                            0
                    ),
                    RepairHelper.RepairStations.SMITHING_TABLE
            );
            unbreakable$scaledWithShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
            materialCost++;
        }

        unbreakable$setRepairMaterialCost(materialCost);
        this.getSlot(3).setStack(outputStack);
    }

    @Inject(
            method = "onTakeOutput",
            at = @At("HEAD"),
            cancellable = true
    )
    private void unbreakable$decrementRepairMaterials(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (unbreakable$getRepairCost() <= 0) {
            return;
        }

        if (!player.isCreative()) {
            this.getSlot(1).getStack().decrement(1);
        }

        this.getSlot(2).getStack().decrement(unbreakable$getRepairCost());
        this.quickMove(player, 2);

        unbreakable$setRepairMaterialCost(0);

        ci.cancel();
    }
}