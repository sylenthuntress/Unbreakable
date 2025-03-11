package sylenthuntress.unbreakable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

import java.util.function.Consumer;

public abstract class ShatterHelper {
    public static void applyShatter(ItemStack stack, int damage, Consumer<Item> breakCallback) {
        if (ShatterHelper.getMaxShatterLevel(stack) == 0) {
            return;
        } else if (ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())) {
            return;
        }

        int shatterLevel = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int maxShatterLevel = ShatterHelper.getMaxShatterLevel(stack);
        int maxDamage = stack.getMaxDamage();

        if (damage <= maxDamage && Unbreakable.CONFIG.allowRepairingShattered() && ShatterHelper.isShattered(stack)) {
            shatterLevel--;

            if (shatterLevel > 0) {
                damage = getMaxDamageWithNegatives(stack);
            }
        } else if (damage > maxDamage && shatterLevel == 0) {
            shatterLevel++;
            if (breakCallback != null) {
                breakCallback.accept(stack.getItem());
            }
        } else if (damage > getMaxDamageWithNegatives(stack) && !ShatterHelper.isMaxShatterLevel(stack)) {
            shatterLevel++;
            damage = maxDamage + 1;
            if (breakCallback != null) {
                breakCallback.accept(stack.getItem());
            }
        }

        stack.set(UnbreakableComponents.SHATTER_LEVEL, Math.min(shatterLevel, maxShatterLevel));

        stack.set(DataComponentTypes.DAMAGE, Math.clamp(damage, 0, getMaxDamageWithNegatives(stack)));
    }

    public static float calculateShatterPenalty(ItemStack stack, float minEffectiveness) {
        return Math.max(
                minEffectiveness,
                1.0F - (float) stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0) / getMaxShatterLevel(stack)
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
        ) >= ShatterHelper.getMaxShatterLevel(stack);
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
        if (!ConfigHelper.isInList$shatterPreventsUse(stack.getRegistryEntry())) {
            return false;
        }

        final int shatterLevel = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);

        if (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1 && shatterLevel == ShatterHelper.getMaxShatterLevel(stack))
            return true;
        else if (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() != -1) {
            return shatterLevel >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD();
        } else {
            return false;
        }
    }

    public static int getMaxDamageWithNegatives(ItemStack stack) {
        return Math.round(stack.getMaxDamage() * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1));
    }
}
