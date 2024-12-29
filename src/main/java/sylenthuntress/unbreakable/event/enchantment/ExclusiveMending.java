package sylenthuntress.unbreakable.event.enchantment;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.util.DataTagKeys.INCOMPATIBLE_ENCHANTMENTS;

public class ExclusiveMending implements EnchantmentEvents.AllowEnchanting {
    @Override
    public TriState allowEnchanting(RegistryEntry<Enchantment> enchantment, ItemStack stack, EnchantingContext context) {
        if (Unbreakable.CONFIG.exclusiveMending() && enchantment.isIn(INCOMPATIBLE_ENCHANTMENTS) &&
                EnchantmentHelper.getEnchantments(stack)
                        .getEnchantments()
                        .stream()
                        .anyMatch(entry -> entry.isIn(INCOMPATIBLE_ENCHANTMENTS))) {
            return TriState.FALSE;
        }
        return TriState.DEFAULT;
    }
}