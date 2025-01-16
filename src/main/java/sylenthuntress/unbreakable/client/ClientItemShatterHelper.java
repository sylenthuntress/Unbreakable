package sylenthuntress.unbreakable.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClientItemShatterHelper {
    public static void sendMessageCantUseItem(ItemStack stack) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), SoundCategory.MASTER, 0.5F, 0.5F);
            player.sendMessage(Text.translatable("unbreakable.shatter.cant_use_item", stack.getName().copy().formatted(Formatting.GOLD)), true);
        }
    }
}
