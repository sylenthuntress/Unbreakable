package sylenthuntress.unbreakable.util;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

import java.util.List;

public abstract class ItemShatterHelper {
    public static float calculateShatterPenalty(ItemStack stack, float minEffectiveness) {
        return Math.max(
                minEffectiveness,
                1.0F - (float) stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0)
                        / getMaxShatterLevel(stack)
        );
    }

    public static float calculateShatterPenalty(ItemStack stack) {
        return calculateShatterPenalty(
                stack,
                Unbreakable.CONFIG.shatterPenalties.STAT_MINIMUM()
        );
    }

    public static int getMaxShatterLevel(ItemStack stack) {
        return stack.getOrDefault(
                UnbreakableComponents.MAX_SHATTER_LEVEL,
                Unbreakable.CONFIG.maxShatterLevel()
                        + getEnchantmentLevel(Enchantments.UNBREAKING, stack.copy())
                        * Unbreakable.CONFIG.enchantmentScaling()
        );
    }

    public static boolean isMaxShatterLevel(ItemStack stack) {
        return stack.getOrDefault(
                UnbreakableComponents.SHATTER_LEVEL, 0
        ) >= ItemShatterHelper.getMaxShatterLevel(stack);
    }

    public static boolean isShattered(ItemStack stack) {
        return stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0) > 0;
    }

    public static int getEnchantmentLevel(RegistryKey<Enchantment> enchantment, ItemStack stack) {
        if (!stack.hasEnchantments()) {
            return 0;
        }

        ItemEnchantmentsComponent enchantments = stack.getEnchantments();

        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (!entry.matchesKey(enchantment)) {
                continue;
            }

            return enchantments.getLevel(entry);
        }

        return 0;
    }

    public static boolean shouldPreventUse(ItemStack stack) {
        final int shatterLevel = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        return isInList$shatterPreventsUse(stack.getRegistryEntry())
                && (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1
                && shatterLevel == ItemShatterHelper.getMaxShatterLevel(stack)
                || Unbreakable.CONFIG.shatterPenalties.THRESHOLD() != -1
                && shatterLevel >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD());
    }

    private static boolean isInStringList(List<String> list, RegistryEntry<Item> item, boolean invertList) {
        return (item.streamTags().map(
                key -> "#" + key.id().toString()
        ).anyMatch(list::contains) || list.contains(item.getIdAsString())
        ) != invertList;
    }

    public static boolean isInList$shatterBlacklist(RegistryEntry<Item> itemEntry) {
        return isInStringList(
                Unbreakable.CONFIG.shatterBlacklist.LIST(),
                itemEntry,
                Unbreakable.CONFIG.shatterBlacklist.INVERT()
        );
    }

    public static boolean isInList$durabilityModifier(RegistryEntry<Item> itemEntry) {
        return isInStringList(
                Unbreakable.CONFIG.durabilityModifier.LIST(),
                itemEntry,
                Unbreakable.CONFIG.durabilityModifier.INVERT()
        );
    }

    public static boolean isInList$shatterPreventsUse(RegistryEntry<Item> itemEntry) {
        return isInStringList(
                Unbreakable.CONFIG.shatterPenalties.LIST(),
                itemEntry,
                Unbreakable.CONFIG.shatterPenalties.INVERT()
        );
    }
}
