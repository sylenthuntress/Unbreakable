package sylenthuntress.unbreakable.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagProvider extends FabricTagProvider<Enchantment> {
    public static final TagKey<Enchantment> INCOMPATIBLE_ENCHANTMENTS = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Unbreakable.MOD_ID, "incompatible_enchantments"));

    public ModEnchantmentTagProvider(FabricDataOutput output, RegistryKey<? extends Registry<Enchantment>> registryKey, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registryKey, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(INCOMPATIBLE_ENCHANTMENTS).add(Enchantments.UNBREAKING, Enchantments.MENDING);
    }
}
