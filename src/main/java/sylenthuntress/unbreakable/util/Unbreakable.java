package sylenthuntress.unbreakable.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.config.UnbreakableConfig;
import sylenthuntress.unbreakable.event.enchantment.ExclusiveMending;
import sylenthuntress.unbreakable.event.item_damage.BlockBreakEvent;
import sylenthuntress.unbreakable.event.item_damage.UseBlockEvent;
import sylenthuntress.unbreakable.event.item_damage.UseItemEvent;

public class Unbreakable implements ModInitializer {
    public static final String MOD_ID = "unbreakable";
    public static final UnbreakableConfig CONFIG = UnbreakableConfig.createAndLoad();
    public static ServerWorld SERVER_WORLD;
    public static DynamicRegistryManager REGISTRY_MANAGER;

    @Override
    public void onInitialize() {
        // Initialize components
        ModComponents.initialize();
        // Initialize events
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer -> {
            SERVER_WORLD = minecraftServer.getWorld(World.OVERWORLD);
            if (SERVER_WORLD != null) REGISTRY_MANAGER = SERVER_WORLD.getRegistryManager();
        }));
        EnchantmentEvents.ALLOW_ENCHANTING.register(new ExclusiveMending());
        UseItemCallback.EVENT.register(new UseItemEvent());
        UseBlockCallback.EVENT.register(new UseBlockEvent());
        PlayerBlockBreakEvents.AFTER.register(new BlockBreakEvent());
    }
}
