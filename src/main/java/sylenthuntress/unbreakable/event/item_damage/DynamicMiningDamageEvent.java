package sylenthuntress.unbreakable.event.item_damage;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.util.Unbreakable;

public class DynamicMiningDamageEvent implements PlayerBlockBreakEvents.After {
    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState block, @Nullable BlockEntity blockEntity) {
        if (Unbreakable.CONFIG.dynamicDamage.MINING()) {
            int itemDamage = (int) block.getHardness(world, pos);
            itemDamage += (int) Math.min(itemDamage, player.getBlockBreakingSpeed(block));
            player.getMainHandStack().damage((int) (itemDamage * Unbreakable.CONFIG.dynamicDamage.MINING_MULTIPLIER()), player);
        }
    }
}
