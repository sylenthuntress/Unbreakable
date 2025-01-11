package sylenthuntress.unbreakable.mixin.item_repair.grindstone;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
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
import sylenthuntress.unbreakable.util.ItemShatterHelper;
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
    private int scaledWithShatterLevel = -1;

    @Unique
    private int calculateRepairCost(long repairCost, ItemStack outputStack, ItemStack inputStack) {
        if (Unbreakable.CONFIG.grindingRepair.COST.ENCHANTMENT_SCALING())
            for (RegistryEntry<Enchantment> enchantment : outputStack.getEnchantments().getEnchantments())
                repairCost += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), outputStack);
        if (Unbreakable.CONFIG.grindingRepair.COST.SHATTER_SCALING()) {
            int oldShatterLevel = inputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            int newShatterLevel = outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            if (scaledWithShatterLevel != newShatterLevel && Unbreakable.CONFIG.anvilRepair.COST.SHATTER_SCALING() && oldShatterLevel > newShatterLevel)
                repairCost = (long) inputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long) outputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + 1;
            scaledWithShatterLevel = newShatterLevel;
        }
        repairCost *= (long) (1 + ((41 - outputStack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0)) * 0.05));
        repairCost *= (long) Unbreakable.CONFIG.grindingRepair.COST.MULTIPLIER();
        return (int) repairCost;
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
                int repairFactor = Math.min(outputStack.getDamage(), outputStack.getMaxDamage() / 6);
                while (outputStack.isDamaged() && experienceLevels > 0) {
                    experienceLevels--;
                    outputStack.setDamage(outputStack.getDamage() - repairFactor);
                    if (Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR())
                        outputStack.set(ModComponents.GRINDING_DEGRADATION, Math.min(40, outputStack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0) + 2));
                    if (Unbreakable.CONFIG.smithingRepair.COST.GRINDING_DECREMENTS_DEGRADATION())
                        outputStack.set(ModComponents.SMITHING_DEGRADATION, Math.max(0, outputStack.getOrDefault(ModComponents.SMITHING_DEGRADATION, 0) - 1));
                    repairFactor = Math.min(outputStack.getDamage(), outputStack.getMaxDamage() / 6);
                }
                setRepairCost(calculateRepairCost((player().experienceLevel - experienceLevels), outputStack, inputStack));
                if (repairCost.get() < player().experienceLevel)
                    this.result.setStack(0, outputStack);
                else this.result.setStack(0, ItemStack.EMPTY);
            }
        }
    }
}
