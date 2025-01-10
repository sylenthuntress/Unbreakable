package sylenthuntress.unbreakable.mixin.item_repair.smithing;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.RepairMaterialRegistry;

import java.util.function.Predicate;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    @Unique
    private int repairMaterialCost = 0;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairableItemsAsBase(Args args) {
        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or(ItemStack::isDamageable));
    }

    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairMaterials(Args args) {
        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or((stack) -> RepairMaterialRegistry.getInstance().isRepairMaterial(stack.getItem())));
    }

    @Inject(method = "method_64654", at = @At("TAIL"))
    private void repairItemLogic(CallbackInfo ci) {
        ItemStack repairBase = this.getSlot(1).getStack();
        ItemStack repairMaterial = this.getSlot(2).getStack();
        ItemStack outputStack = repairBase.copy();
        if (repairBase.isDamaged() && repairBase.canRepairWith(repairMaterial)) {
            int repairFactor = calculateRepairFactor(outputStack);
            int materialCost = 0;
            while (repairFactor > 0 && materialCost < repairMaterial.getCount()) {
                outputStack.setDamage(outputStack.getDamage() - repairFactor);
                outputStack.set(ModComponents.SMITHING_REPAIR_FACTOR, Math.min(20, outputStack.getOrDefault(ModComponents.SMITHING_REPAIR_FACTOR, 0) + 1));
                repairFactor = calculateRepairFactor(outputStack);
                materialCost++;
            }
            repairMaterialCost = materialCost;
            this.getSlot(3).setStack(outputStack);
        }
    }

    @Unique
    private int calculateRepairFactor(ItemStack stack) {
        return (int) Math.round(Math.min(stack.getDamage(), stack.getMaxDamage() / 4) * ((21 - stack.getOrDefault(ModComponents.SMITHING_REPAIR_FACTOR, 0)) * 0.05));
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void decrementRepairMaterials(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (repairMaterialCost > 0) {
            this.getSlot(1).getStack().decrement(1);
            this.getSlot(2).getStack().decrement(repairMaterialCost);
            repairMaterialCost = 0;
            ci.cancel();
        }
    }
}