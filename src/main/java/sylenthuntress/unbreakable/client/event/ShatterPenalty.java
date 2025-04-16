package sylenthuntress.unbreakable.client.event;

import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.client.ClientItemShatterHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;

public class ShatterPenalty implements AttackBlockCallback, UseBlockCallback, AttackEntityCallback, UseEntityCallback, UseItemCallback {
    public static void registerAll() {
        ShatterPenalty shatterPenalty = new ShatterPenalty();

        UseItemCallback.EVENT.register(shatterPenalty);
        UseBlockCallback.EVENT.register(shatterPenalty);
        UseEntityCallback.EVENT.register(shatterPenalty);
        AttackEntityCallback.EVENT.register(shatterPenalty);
        AttackBlockCallback.EVENT.register(shatterPenalty);
    }

    // Disable block hit
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

    // Disable block use
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        ItemStack stack = player.getStackInHand(hand);

        boolean configDisableInteraction = Unbreakable.CONFIG.shatterPenalties.BLOCK_USE();
        boolean playerIsCheating = !(player.isInCreativeMode() && player.isSpectator());
        boolean shouldPreventUsingStack = ShatterHelper.shouldPreventUse(stack);

        if (configDisableInteraction && playerIsCheating && shouldPreventUsingStack) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    // Disable entity hit, or use
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        ItemStack stack = player.getStackInHand(hand);

        boolean configDisableInteraction = hand.equals(Hand.MAIN_HAND)
                ? Unbreakable.CONFIG.shatterPenalties.ENTITY_HIT()
                : Unbreakable.CONFIG.shatterPenalties.ENTITY_USE();
        boolean playerIsCheating = !(player.isInCreativeMode() && player.isSpectator());
        boolean shouldPreventUsingStack = ShatterHelper.shouldPreventUse(stack);

        if (configDisableInteraction && playerIsCheating && shouldPreventUsingStack) {
            ClientItemShatterHelper.sendMessageCantUseItem(stack);
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    // Disable item use
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
