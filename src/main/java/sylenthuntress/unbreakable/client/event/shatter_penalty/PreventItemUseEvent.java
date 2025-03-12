package sylenthuntress.unbreakable.client.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;

public class PreventItemUseEvent implements UseItemCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        boolean configDisableInteraction = Unbreakable.CONFIG.shatterPenalties.ITEM_USE();
        boolean playerIsCheating = !(player.isInCreativeMode() && player.isSpectator());
        boolean shouldPreventUsingStack = ShatterHelper.shouldPreventUse(stack);

        if (configDisableInteraction && playerIsCheating && shouldPreventUsingStack) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
