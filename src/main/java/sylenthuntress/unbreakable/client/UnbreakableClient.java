package sylenthuntress.unbreakable.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.player.*;
import sylenthuntress.unbreakable.client.event.DisplayShatterTooltip;
import sylenthuntress.unbreakable.client.event.shatter_penalty.*;


public class UnbreakableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // FabricLoader.getInstance().getModContainer(Unbreakable.MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(Unbreakable.modIdentifier("shattered_textures"), container, Text.translatable("resourcePack.unbreakable.shattered_textures.name"), ResourcePackActivationType.DEFAULT_ENABLED));
        ItemTooltipCallback.EVENT.register(new DisplayShatterTooltip());
        UseItemCallback.EVENT.register(new PreventItemUseEvent());
        UseBlockCallback.EVENT.register(new PreventBlockUseEvent());
        UseEntityCallback.EVENT.register(new PreventEntityUseEvent());
        AttackEntityCallback.EVENT.register(new PreventEntityAttackEvent());
        AttackBlockCallback.EVENT.register(new PreventBlockHitEvent());
    }
}