package sylenthuntress.unbreakable.util;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

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
        return ConfigHelper.isInList$shatterPreventsUse(stack.getRegistryEntry())
                && (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1
                && shatterLevel == ItemShatterHelper.getMaxShatterLevel(stack)
                || Unbreakable.CONFIG.shatterPenalties.THRESHOLD() != -1
                && shatterLevel >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD());
    }
}
