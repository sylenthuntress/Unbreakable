package sylenthuntress.unbreakable.event.enchantment;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.Unbreakable;

import java.util.List;

public class ExclusiveMending implements EnchantmentEvents.AllowEnchanting {
    @Override
    public TriState allowEnchanting(RegistryEntry<Enchantment> enchantment, ItemStack stack, EnchantingContext context) {
        List<RegistryKey<?>> incompatibleEnchantments = List.of(Enchantments.MENDING, Enchantments.UNBREAKING);

        boolean configExclusiveMending = Unbreakable.CONFIG.exclusiveMending();
        boolean targetEnchantmentIsExclusive = enchantment.matches(incompatibleEnchantments::contains);
        boolean stackHasExclusiveEnchant = EnchantmentHelper.getEnchantments(stack)
                .getEnchantments()
                .stream()
                .anyMatch(entry -> entry.matches(incompatibleEnchantments::contains));

        return configExclusiveMending && targetEnchantmentIsExclusive && stackHasExclusiveEnchant
                ? TriState.FALSE // Disable enchanting if incompatible
                : TriState.DEFAULT; // Pass to other modded checks if not
    }
}