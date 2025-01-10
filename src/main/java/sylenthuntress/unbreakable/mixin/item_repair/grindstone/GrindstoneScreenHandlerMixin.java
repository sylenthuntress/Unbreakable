package sylenthuntress.unbreakable.mixin.item_repair.grindstone;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.access.GrindstoneScreenHandlerAccess;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements GrindstoneScreenHandlerAccess {
    @Unique
    protected Property repairCost = Property.create();
    @Shadow
    @Final
    private Inventory result;

    protected GrindstoneScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Unique
    public int unbreakable$getRepairCost() {
        return this.repairCost.get();
    }

    @Unique
    private void setRepairCost(int repairCost) {
        this.repairCost.set(repairCost);
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    void repairWithXp(CallbackInfo ci) {
        if (Unbreakable.CONFIG.grindingRepair.ALLOW()) {
            setRepairCost(0);
            int experienceLevels = player().experienceLevel;
            ItemStack inputStack = this.getSlot(0).getStack();
            ItemStack outputStack = inputStack.copy();
            if (!inputStack.isEmpty()
                    && this.getSlot(1).getStack().isEmpty()
                    && experienceLevels > 0) {
                int repairFactor = calculateRepairFactor(outputStack);
                while (outputStack.isDamaged() && experienceLevels > 0) {
                    experienceLevels--;
                    outputStack.setDamage(outputStack.getDamage() - repairFactor);
                    if (Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR())
                        outputStack.set(ModComponents.GRINDING_DEGRADATION, Math.min(20, outputStack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0) + 1));
                    calculateRepairFactor(outputStack);
                }
                this.result.setStack(0, outputStack);
                setRepairCost(player().experienceLevel - experienceLevels);
            }
        }
    }

    @Unique
    private int calculateRepairFactor(ItemStack stack) {
        int repairFactor = (int) Math.round(Math.min(stack.getDamage(), stack.getMaxDamage() / 6) * ((41 - stack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0)) * 0.05));
        repairFactor = (int) (repairFactor * Unbreakable.CONFIG.grindingRepair.COST.MULTIPLIER());
        return repairFactor;
    }
}
