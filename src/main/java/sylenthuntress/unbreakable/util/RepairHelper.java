package sylenthuntress.unbreakable.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

import java.util.ArrayList;
import java.util.List;

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

    public static int calculateRepairFactor(int repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, RepairStation repairStation) {
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

    private static int calculateRepairConstant(double repairConstant, ItemStack outputStack, ItemStack inputStack, boolean scaledWithShatterLevel, RepairStation repairStation) {
        boolean shatterScaling = false;
        boolean enchantmentScaling = false;
        boolean degradeRepairFactor = false;
        float costMultiplier = 1;
        switch (repairStation) {
            case RepairStation.SMITHING_TABLE -> {
                shatterScaling = Unbreakable.CONFIG.smithingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.smithingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.smithingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.smithingRepair.COST.MULTIPLIER();
            }
            case RepairStation.GRINDSTONE -> {
                shatterScaling = Unbreakable.CONFIG.grindingRepair.COST.SHATTER_SCALING();
                enchantmentScaling = Unbreakable.CONFIG.grindingRepair.COST.ENCHANTMENT_SCALING();
                degradeRepairFactor = Unbreakable.CONFIG.grindingRepair.COST.DEGRADE_REPAIR_FACTOR();
                costMultiplier = Unbreakable.CONFIG.grindingRepair.COST.MULTIPLIER();
            }
        }
        int oldShatterLevel = inputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int newShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        if (!scaledWithShatterLevel && shatterScaling && oldShatterLevel > newShatterLevel)
            repairConstant += inputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + outputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0);
        if (enchantmentScaling)
            for (RegistryEntry<Enchantment> enchantment : outputStack.getEnchantments().getEnchantments())
                repairConstant += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), outputStack);
        if (degradeRepairFactor)
            if (repairStation == RepairStation.GRINDSTONE)
                repairConstant /= 1 + (outputStack.getOrDefault(UnbreakableComponents.DEGRADATION_GRINDING, 0) * 0.1);
            else repairConstant /= 1 + (outputStack.getOrDefault(UnbreakableComponents.DEGRADATION_SMITHING, 0) * 0.1);
        repairConstant *= costMultiplier;
        return (int) Math.round(repairConstant);
    }

    public enum RepairStation {
        SMITHING_TABLE("smithing_table", 0, Blocks.SMITHING_TABLE),
        GRINDSTONE("grindstone", 1, Blocks.GRINDSTONE);

        private final Identifier name;
        private final int id;
        private final List<Block> blocks;

        RepairStation(final String name, final int id, final List<Block> blocks) {
            this.name = Unbreakable.modIdentifier(name);
            this.id = id;
            this.blocks = blocks;
        }

        RepairStation(final String name, final int id, final Block blocks) {
            this(name, id, List.of(blocks));
        }
    }
}
