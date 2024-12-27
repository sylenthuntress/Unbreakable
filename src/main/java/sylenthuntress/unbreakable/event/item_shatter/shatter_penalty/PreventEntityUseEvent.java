package sylenthuntress.unbreakable.event.item_shatter.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

public class PreventEntityUseEvent implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack)) {
            player.stopUsingItem();
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
