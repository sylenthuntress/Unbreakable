package sylenthuntress.unbreakable.event.item_shatter.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

public class PreventItemUseEvent implements UseItemCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack)) {
            player.stopUsingItem();
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
