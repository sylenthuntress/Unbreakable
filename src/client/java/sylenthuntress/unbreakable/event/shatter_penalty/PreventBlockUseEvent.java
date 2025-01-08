package sylenthuntress.unbreakable.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.util.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

public class PreventBlockUseEvent implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (Unbreakable.CONFIG.shatterPenalties.BLOCK_USE() && !player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack)) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
