package sylenthuntress.unbreakable.event.item_shatter.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

public class PreventBlockUseEvent implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack)) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
