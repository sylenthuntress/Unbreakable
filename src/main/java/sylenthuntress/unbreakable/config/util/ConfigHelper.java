package sylenthuntress.unbreakable.config.util;

import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import sylenthuntress.unbreakable.Unbreakable;

import java.util.List;

public abstract class ConfigHelper {
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
