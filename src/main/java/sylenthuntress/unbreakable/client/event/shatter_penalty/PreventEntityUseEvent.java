package sylenthuntress.unbreakable.client.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

public class PreventEntityUseEvent implements UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (Unbreakable.CONFIG.shatterPenalties.ENTITY_USE() && !player.isInCreativeMode() && !player.isSpectator() && ItemShatterHelper.shouldPreventUse(stack)) {
            player.stopUsingItem();
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
