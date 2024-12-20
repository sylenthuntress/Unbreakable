package sylenthuntress.unbreakable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class ItemShatterHelper {
    public static float calculateShatterPenalty(ItemStack stack, float minimum) {
        return Math.max(minimum, 1.0F - ((float) stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) / getMaxShatterLevel(stack)));
    }

    public static float calculateShatterPenalty(ItemStack stack) {
        return calculateShatterPenalty(stack, Unbreakable.CONFIG.shatterPenalties.STAT_MINIMUM());
    }

    public static int getMaxShatterLevel(ItemStack stack) {
        return Math.max(stack.getOrDefault(ModComponents.MAX_SHATTER_LEVEL, Unbreakable.CONFIG.maxShatterLevel() + (getEnchantmentLevel(Enchantments.UNBREAKING, stack.copy()) * Unbreakable.CONFIG.enchantmentScaling())), stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0));
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

    public static double getAttribute(ItemStack stack, RegistryEntry<EntityAttribute> attribute, double base) {
        if (!stack.isEmpty()) {
            AttributeModifiersComponent attributeModifierComponent = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
            assert attributeModifierComponent != null;
            for (AttributeModifiersComponent.Entry entry : attributeModifierComponent.modifiers()) {
                RegistryKey<EntityAttribute> target_attribute = attribute.getKey().get();
                if (entry.attribute().matchesKey(target_attribute)) {
                    base += entry.modifier().value();
                }
            }
        }
        return base;
    }

    public static boolean isInList$shatterPreventsUse(RegistryEntry<Item> item, ItemStack stack) {
        List<String> itemIdAndTags = new java.util.ArrayList<>(List.of(item.getIdAsString()));
        List<String> configList = Unbreakable.CONFIG.shatterPenalties.LIST();
        item.streamTags().toList().forEach(itemTagKey -> itemIdAndTags.add("#" + itemTagKey.id().toString()));
        itemIdAndTags.retainAll(configList);
        return Unbreakable.CONFIG.shatterPenalties.INVERT() == itemIdAndTags.isEmpty() && ((Unbreakable.CONFIG.shatterPenalties.THRESHOLD() > -1 && stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) >= Unbreakable.CONFIG.shatterPenalties.THRESHOLD()) || (Unbreakable.CONFIG.shatterPenalties.THRESHOLD() == -1 && isMaxShatterLevel(stack)));
    }
}
