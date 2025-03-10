package sylenthuntress.unbreakable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import sylenthuntress.unbreakable.RepairMethods;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairHelper {
    protected static List<RegistryEntryList<Item>> repairMaterials = new ArrayList<>();

    public static boolean isRepairMaterial(ItemStack stack) {
        for (RegistryEntryList<Item> registryEntryList : repairMaterials) {
            if (stack.isIn(registryEntryList)) return true;
        }
        return false;
    }

    public static void addRepairMaterial(RegistryEntryList<Item> items) {
        repairMaterials.add(items);
    }

    public static int calculateRepairFactor(int repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, RepairMethods repairMethods) {
        return Math.min(
                outputStack.getDamage(),
                outputStack.getMaxDamage() /
                        calculateRepairConstant(
                                repairConstant,
                                outputStack,
                                inputStack,
                                scaledWithShatterLevel,
                                repairMethods
                        )
        );
    }

    private static int calculateRepairConstant(double repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, RepairMethods repairMethods) {
        boolean shatterScaling = false;
        boolean enchantmentScaling = false;
        boolean degradeRepairFactor = false;
        float costMultiplier = 1;

        switch (repairMethods) {
            case RepairMethods.SMITHING_TABLE -> {
                shatterScaling = Unbreakable.CONFIG.smithingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.smithingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.smithingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.smithingRepair.COST.MULTIPLIER();
            }
            case RepairMethods.GRINDSTONE -> {
                shatterScaling = Unbreakable.CONFIG.grindingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.grindingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.grindingRepair.COST.MULTIPLIER();
            }
        }

        int oldShatterLevel = inputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int newShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);

        if (!scaledWithShatterLevel && shatterScaling && oldShatterLevel > newShatterLevel) {
            repairConstant += inputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0)
                    + outputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
        }
        if (enchantmentScaling) {
            for (RegistryEntry<Enchantment> enchantment : outputStack.getEnchantments().getEnchantments()) {
                repairConstant += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), outputStack);
            }
        }
        if (degradeRepairFactor) {
            var degradationMap = new HashMap<>(outputStack.getOrDefault(UnbreakableComponents.DEGRADATION, Map.of()));
            repairConstant *= 1 + degradationMap.getOrDefault(repairMethods.getName().toString(), 0) * 0.1F;
        }

        repairConstant *= costMultiplier;
        return (int) Math.round(repairConstant);
    }
}
