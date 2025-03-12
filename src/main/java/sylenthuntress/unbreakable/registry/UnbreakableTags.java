package sylenthuntress.unbreakable.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import sylenthuntress.unbreakable.Unbreakable;

@SuppressWarnings("unused")
public abstract class UnbreakableTags {
    public static final TagKey<Item> SHATTER_BLACKLIST = TagKey.of(
            RegistryKeys.ITEM,
            Unbreakable.modIdentifier("shatter_blacklist")
    );
    public static final TagKey<Item> BREAKABLE_ITEMS = TagKey.of(
            RegistryKeys.ITEM,
            Unbreakable.modIdentifier("retains_breaking")
    );
    public static final TagKey<Item> PREVENT_USE_WHEN_SHATTERED = TagKey.of(
            RegistryKeys.ITEM,
            Unbreakable.modIdentifier("prevent_use_when_shattered")
    );
    public static final TagKey<Item> GOLDEN_EQUIPMENT = TagKey.of(
            RegistryKeys.ITEM,
            Unbreakable.modIdentifier("golden_equipment")
    );
}
