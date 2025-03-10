package sylenthuntress.unbreakable.client.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;

public class PreventBlockHitEvent implements AttackBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos blockPos, Direction direction) {
        ItemStack stack = player.getMainHandStack();

        boolean configDisableInteraction = Unbreakable.CONFIG.shatterPenalties.BLOCK_HIT();
        boolean playerIsCheating = !(player.isInCreativeMode() && player.isSpectator());
        boolean shouldPreventUsingStack = ShatterHelper.shouldPreventUse(stack);

        if (configDisableInteraction && playerIsCheating && shouldPreventUsingStack) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
