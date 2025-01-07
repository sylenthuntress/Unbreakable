package sylenthuntress.unbreakable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public abstract class ItemShatterHelper {
    public static float calculateShatterPenalty(ItemStack stack, float minimum) {
        return Math.max(minimum, 1.0F - ((float) stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) / getMaxShatterLevel(stack)));
    }

    public static float calculateShatterPenalty(ItemStack stack) {
        return calculateShatterPenalty(stack, Unbreakable.CONFIG.shatterPenalties.STAT_MINIMUM());
    }

    public static int getMaxShatterLevel(ItemStack stack) {
        return stack.getOrDefault(ModComponents.MAX_SHATTER_LEVEL, Unbreakable.CONFIG.maxShatterLevel() + (getEnchantmentLevel(Enchantments.UNBREAKING, stack.copy()) * Unbreakable.CONFIG.enchantmentScaling()));
    }

    public static boolean isMaxShatterLevel(ItemStack stack) {
        return stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) >= ItemShatterHelper.getMaxShatterLevel(stack);
    }

    public static boolean isShattered(ItemStack stack) {
        return stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) > 0;
    }

    public static int getEnchantmentLevel(RegistryKey<Enchantment> enchantment, ItemStack stack) {
        int level = 0;
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (RegistryEntry<Enchantment> entry : itemEnchantmentsComponent.getEnchantments()) {
            if (entry.matchesKey(enchantment)) {
                level = itemEnchantmentsComponent.getLevel(entry);
            }
        }
        return level;
    }

    public static boolean shouldPreventUse(RegistryEntry<Item> item, ItemStack stack) {
        int shatterLevel = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
        return isInList$shatterPreventsUse(item, stack) && (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1 ? shatterLevel == ItemShatterHelper.getMaxShatterLevel(stack) : shatterLevel >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD());
    }

    private static boolean isInStringList(List<String> list, RegistryEntry<Item> item, boolean invertList) {
        List<String> itemIdAndTags = new java.util.ArrayList<>(List.of(item.getIdAsString()));
        List<String> configList = list;
        item.streamTags().toList().forEach(itemTagKey -> itemIdAndTags.add("#" + itemTagKey.id().toString()));
        itemIdAndTags.retainAll(configList);
        return invertList == itemIdAndTags.isEmpty();
    }

    public static boolean isInList$shatterBlacklist(RegistryEntry<Item> item) {
        return isInStringList(Unbreakable.CONFIG.shatterBlacklist.LIST(), item, Unbreakable.CONFIG.shatterBlacklist.INVERT());
    }

    public static boolean isInList$durabilityModifier(RegistryEntry<Item> item) {
        return isInStringList(Unbreakable.CONFIG.durabilityModifier.LIST(), item, Unbreakable.CONFIG.durabilityModifier.INVERT());
    }

    public static boolean isInList$shatterPreventsUse(RegistryEntry<Item> item, ItemStack stack) {
        return isInStringList(Unbreakable.CONFIG.shatterPenalties.LIST(), item, Unbreakable.CONFIG.shatterPenalties.INVERT()) && ((Unbreakable.CONFIG.shatterPenalties.THRESHOLD() > -1 && stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD()) || (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1 && isMaxShatterLevel(stack)));
    }
}
