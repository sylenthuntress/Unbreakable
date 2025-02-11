package sylenthuntress.unbreakable;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.minecraft.util.Identifier;
import sylenthuntress.unbreakable.config.UnbreakableConfig;
import sylenthuntress.unbreakable.event.enchantment.ExclusiveMending;
import sylenthuntress.unbreakable.event.item_damage.DynamicMiningDamageEvent;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;

public class Unbreakable implements ModInitializer {
    public static final String MOD_ID = "unbreakable";
    public static final UnbreakableConfig CONFIG = UnbreakableConfig.createAndLoad();

    public static Identifier modIdentifier(String name) {
        return Identifier.of(MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        UnbreakableComponents.initialize();

        EnchantmentEvents.ALLOW_ENCHANTING.register(new ExclusiveMending());
        PlayerBlockBreakEvents.AFTER.register(new DynamicMiningDamageEvent());
    }
}
