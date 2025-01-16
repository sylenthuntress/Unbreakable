package sylenthuntress.unbreakable.client.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

public class PreventBlockHitEvent implements AttackBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos blockPos, Direction direction) {
        ItemStack stack = player.getMainHandStack();
        if (Unbreakable.CONFIG.shatterPenalties.BLOCK_HIT() && !player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack)) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
