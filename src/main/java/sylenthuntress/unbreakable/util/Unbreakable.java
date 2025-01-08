package sylenthuntress.unbreakable.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import sylenthuntress.unbreakable.config.UnbreakableConfig;
import sylenthuntress.unbreakable.event.enchantment.ExclusiveMending;
import sylenthuntress.unbreakable.event.item_damage.DynamicMiningDamageEvent;

public class Unbreakable implements ModInitializer {
    public static final String MOD_ID = "unbreakable";
    public static final UnbreakableConfig CONFIG = UnbreakableConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // Initialize components
        ModComponents.initialize();
        // Initialize events
        EnchantmentEvents.ALLOW_ENCHANTING.register(new ExclusiveMending());
        PlayerBlockBreakEvents.AFTER.register(new DynamicMiningDamageEvent());
    }
}
