package sylenthuntress.unbreakable.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import sylenthuntress.unbreakable.config.UnbreakableConfig;
import sylenthuntress.unbreakable.event.enchantment.ExclusiveMending;
import sylenthuntress.unbreakable.event.item_damage.DynamicMiningDamageEvent;
import sylenthuntress.unbreakable.event.item_shatter.shatter_penalty.PreventBlockUseEvent;
import sylenthuntress.unbreakable.event.item_shatter.shatter_penalty.PreventEntityUseEvent;
import sylenthuntress.unbreakable.event.item_shatter.shatter_penalty.PreventItemUseEvent;

public class Unbreakable implements ModInitializer {
    public static final String MOD_ID = "unbreakable";
    public static final UnbreakableConfig CONFIG = UnbreakableConfig.createAndLoad();

    @Override
    public void onInitialize() {
        // Initialize components
        ModComponents.initialize();
        // Initialize events
        EnchantmentEvents.ALLOW_ENCHANTING.register(new ExclusiveMending());
        UseItemCallback.EVENT.register(new PreventItemUseEvent());
        UseBlockCallback.EVENT.register(new PreventBlockUseEvent());
        UseEntityCallback.EVENT.register(new PreventEntityUseEvent());
        PlayerBlockBreakEvents.AFTER.register(new DynamicMiningDamageEvent());
    }
}
