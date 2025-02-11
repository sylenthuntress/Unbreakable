package sylenthuntress.unbreakable.event.item_damage;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sylenthuntress.unbreakable.Unbreakable;

public class DynamicMiningDamageEvent implements PlayerBlockBreakEvents.After {
    @Override
    public void afterBlockBreak(
            World world,
            PlayerEntity player,
            BlockPos pos,
            BlockState block,
            @Nullable BlockEntity blockEntity) {
        if (!Unbreakable.CONFIG.dynamicDamage.MINING()) {
            return;
        }

        int itemDamage = (int) Math.ceil(block.getHardness(world, pos));
        itemDamage += (int) Math.min(itemDamage, player.getBlockBreakingSpeed(block));
        itemDamage -= 1;

        if (!player.canHarvest(block)) {
            itemDamage *= 2;
        }

        player.getMainHandStack().damage(
                Math.round(
                        itemDamage * Unbreakable.CONFIG.dynamicDamage.MINING_MULTIPLIER()
                ),
                player,
                EquipmentSlot.MAINHAND
        );
    }
}
