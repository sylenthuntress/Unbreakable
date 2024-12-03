package sylenthuntress.unbreakable.event.item_damage;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;

public class UseItemEvent implements UseItemCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.isInList$shatterPreventsUse(stack.getRegistryEntry(), stack) && stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) > 0) {
            player.stopUsingItem();
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
