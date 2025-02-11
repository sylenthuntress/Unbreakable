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
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.access.GrindstoneScreenHandlerAccess;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.RepairHelper;

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
    private void unbreakable$setRepairCost(int repairCost) {
        this.repairCost.set(repairCost);
    }

    @Inject(
            method = "updateResult",
            at = @At("TAIL")
    )
    void unbreakable$repairWithXp(CallbackInfo ci) {
        if (!Unbreakable.CONFIG.grindingRepair.ALLOW()) {
            return;
        }

        unbreakable$setRepairCost(0);
        int experienceLevels = player().experienceLevel;
        int newShatterLevel = -1;
        final ItemStack inputStack = this.getSlot(0).getStack();
        final ItemStack outputStack = inputStack.copy();

        if (inputStack.isEmpty()
                || !this.getSlot(1).getStack().isEmpty()
                || experienceLevels <= 0) {
            return;
        }

        int repairFactor = RepairHelper.calculateRepairFactor(
                6,
                outputStack,
                inputStack,
                newShatterLevel == outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0),
                RepairHelper.RepairStations.GRINDSTONE
        );

        newShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);

        while (outputStack.isDamaged()) {
            experienceLevels--;
            outputStack.setDamage(outputStack.getDamage() - repairFactor);

            if (Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR()) {
                outputStack.set(
                        UnbreakableComponents.GRINDING_DEGRADATION,
                        Math.min(40,
                                outputStack.getOrDefault(
                                        UnbreakableComponents.GRINDING_DEGRADATION,
                                        0
                                ) + 2
                        )
                );
            }
            if (Unbreakable.CONFIG.smithingRepair.COST.GRINDING_DECREMENTS_DEGRADATION()) {
                outputStack.set(
                        UnbreakableComponents.SMITHING_DEGRADATION,
                        Math.max(0,
                                outputStack.getOrDefault(
                                        UnbreakableComponents.SMITHING_DEGRADATION,
                                        0
                                ) - 1
                        )
                );
            }

            repairFactor = RepairHelper.calculateRepairFactor(
                    6,
                    outputStack,
                    inputStack,
                    newShatterLevel == outputStack.getOrDefault(
                            UnbreakableComponents.SHATTER_LEVEL,
                            0
                    ),
                    RepairHelper.RepairStations.GRINDSTONE
            );
            newShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        }

        unbreakable$setRepairCost(player().experienceLevel - experienceLevels);

        if (unbreakable$getRepairCost() <= player().experienceLevel || player().isCreative()) {
            this.result.setStack(0, outputStack);
        } else {
            this.result.setStack(0, ItemStack.EMPTY);
        }
    }
}
