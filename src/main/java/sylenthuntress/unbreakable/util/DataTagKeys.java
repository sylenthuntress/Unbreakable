package sylenthuntress.unbreakable.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public abstract class DataTagKeys {
    public static final TagKey<Item> SHATTER_BLACKLIST = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "shatter_blacklist"));
    public static final TagKey<Item> BREAKABLE_ITEMS = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "retains_breaking"));
    public static final TagKey<Item> PREVENT_USE_WHEN_SHATTERED = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "prevent_use_when_shattered"));
    public static final TagKey<Item> GOLDEN_EQUIPMENT = TagKey.of(RegistryKeys.ITEM, Identifier.of(Unbreakable.MOD_ID, "golden_equipment"));
    public static final TagKey<Enchantment> INCOMPATIBLE_ENCHANTMENTS = TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(Unbreakable.MOD_ID, "incompatible_enchantments"));

}
