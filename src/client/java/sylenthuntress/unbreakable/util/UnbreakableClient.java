package sylenthuntress.unbreakable.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import sylenthuntress.unbreakable.event.DisplayShatterTooltip;

@Environment(EnvType.CLIENT)
public class UnbreakableClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // FabricLoader.getInstance().getModContainer(Unbreakable.MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(Unbreakable.MOD_ID, "shattered_textures"), container, Text.translatable("resourcePack.unbreakable.shattered_textures.name"), ResourcePackActivationType.DEFAULT_ENABLED));
        ItemTooltipCallback.EVENT.register(new DisplayShatterTooltip());
    }
}