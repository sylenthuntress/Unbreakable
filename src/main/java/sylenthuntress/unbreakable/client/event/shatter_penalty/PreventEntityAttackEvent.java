package sylenthuntress.unbreakable.client.event.shatter_penalty;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;

public class PreventEntityAttackEvent implements AttackEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack stack = player.getStackInHand(hand);

        boolean configDisableInteraction = Unbreakable.CONFIG.shatterPenalties.ENTITY_HIT();
        boolean playerIsCheating = !(player.isInCreativeMode() && player.isSpectator());
        boolean shouldPreventUsingStack = ShatterHelper.shouldPreventUse(stack);

        if (configDisableInteraction && playerIsCheating && shouldPreventUsingStack) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
