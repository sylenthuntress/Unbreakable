package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
    @ModifyVariable(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;II)V", at = @At("STORE"))
    private Vec3d FishingBobberEntity$applyShatterPenalty(Vec3d vec3d, PlayerEntity thrower) {
        ItemStack stack = thrower.getActiveItem();
        double penaltyMultiplier = 1;
        if (stack != null && ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.PROJECTILES()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return vec3d.multiply(penaltyMultiplier, penaltyMultiplier, penaltyMultiplier);
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getLuck()F"))
    private float use$applyShatterPenalty(float original, ItemStack stack) {
        double penaltyMultiplier = 1;
        if (stack != null && ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.ARMOR_TOUGHNESS()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return (int) (original * penaltyMultiplier);
    }
}
