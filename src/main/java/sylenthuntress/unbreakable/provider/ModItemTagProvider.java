package sylenthuntress.unbreakable.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public static final TagKey<Item> SHATTER_BLACKLIST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "shatter_blacklist"));
    public static final TagKey<Item> BREAKABLE_ITEMS = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "retains_breaking"));

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        getOrCreateTagBuilder(SHATTER_BLACKLIST)
                .add(
                        Items.WOLF_ARMOR,
                        Items.WOODEN_AXE,
                        Items.WOODEN_HOE,
                        Items.WOODEN_PICKAXE,
                        Items.WOODEN_SHOVEL,
                        Items.WOODEN_SWORD,
                        Items.STONE_PICKAXE,
                        Items.STONE_AXE,
                        Items.STONE_HOE,
                        Items.STONE_SHOVEL,
                        Items.STONE_SWORD
                );
        getOrCreateTagBuilder(BREAKABLE_ITEMS).
                addTag(SHATTER_BLACKLIST);
    }
}
