package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.util.ItemShatterHelper;


@Mixin(FishingBobberEntity.class)
public abstract class Mixin_FishingBobberEntity {
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V",
            at = @At("STORE")
    )
    private Vec3d unbreakable$applyCastSpeedShatterPenalty(Vec3d original, PlayerEntity thrower) {
        ItemStack stack = thrower.getMainHandStack();
        if (!stack.isIn(ConventionalItemTags.FISHING_ROD_TOOLS)) {
            stack = thrower.getOffHandStack();
        }

        if (!ItemShatterHelper.isShattered(stack)
                || ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())
                || !Unbreakable.CONFIG.shatterPenalties.PROJECTILES()) {
            return original;
        }

        final double penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        return original.multiply(penaltyMultiplier);
    }

    @ModifyExpressionValue(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getLuck()F"
            )
    )
    private float unbreakable$applyFishingLuckShatteredPenalty(float original, ItemStack stack) {
        if (!ItemShatterHelper.isShattered(stack)
                || ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())
                || !Unbreakable.CONFIG.shatterPenalties.FISHING_LUCK()) {
            return original;
        }

        return Math.round(original * ItemShatterHelper.calculateShatterPenalty(stack));
    }
}
