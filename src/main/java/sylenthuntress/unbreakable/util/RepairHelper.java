package sylenthuntress.unbreakable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;

public class RepairHelper {
    private static RepairHelper instance = null;
    protected List<RegistryEntryList<Item>> repairMaterials = new ArrayList<>();

    private RepairHelper() {

    }

    public static synchronized RepairHelper getRegistryInstance() {
        if (instance == null)
            instance = new RepairHelper();
        return instance;
    }

    public boolean isRepairMaterial(ItemStack stack) {
        for (RegistryEntryList<Item> registryEntryList : repairMaterials) {
            if (stack.isIn(registryEntryList)) return true;
        }
        return false;
    }

    public boolean isRepairMaterial(Item item) {
        return isRepairMaterial(item.getDefaultStack());
    }

    public void addRepairMaterial(RegistryEntryList<Item> items) {
        repairMaterials.add(items);
    }

    public static int calculateRepairFactor(int repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, int repairStation) {
        return Math.min(
                outputStack.getDamage(),
                outputStack.getMaxDamage() /
                        calculateRepairConstant(
                                repairConstant,
                                outputStack,
                                inputStack,
                                scaledWithShatterLevel,
                                repairStation
                        )
        );
    }

    private static int calculateRepairConstant(double repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, int repairStation) {
        boolean shatterScaling = false;
        boolean enchantmentScaling = false;
        boolean degradeRepairFactor = false;
        float costMultiplier = 1;
        switch (repairStation) {
            case (RepairStations.SMITHING_TABLE) -> {
                shatterScaling = Unbreakable.CONFIG.smithingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.smithingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.smithingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.smithingRepair.COST.MULTIPLIER();
            }
            case (RepairStations.GRINDSTONE) -> {
                shatterScaling = Unbreakable.CONFIG.grindingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.grindingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.grindingRepair.COST.MULTIPLIER();
            }
        }
        int oldShatterLevel = inputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
        int newShatterLevel = outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
        if (!scaledWithShatterLevel && shatterScaling && oldShatterLevel > newShatterLevel)
            repairConstant = inputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + outputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + 1;
        if (enchantmentScaling)
            for (RegistryEntry<Enchantment> enchantment : outputStack.getEnchantments().getEnchantments())
                repairConstant += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), outputStack);
        if (degradeRepairFactor)
            if (repairStation == RepairStations.GRINDSTONE)
                repairConstant /= 1 + (outputStack.getOrDefault(ModComponents.GRINDING_DEGRADATION, 0) * 0.1);
            else repairConstant /= 1 + (outputStack.getOrDefault(ModComponents.SMITHING_DEGRADATION, 0) * 0.1);
        repairConstant *= costMultiplier;
        return (int) Math.round(repairConstant);
    }

    public abstract static class RepairStations {
        public static final int ANVIL = 1;
        public static final int SMITHING_TABLE = 2;
        public static final int GRINDSTONE = 3;
    }
}
