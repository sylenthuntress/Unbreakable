package sylenthuntress.unbreakable.mixin.durability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    int cachedDamage;

    @Inject(method = "calcGlidingVelocity", at = @At("TAIL"))
    void damageElytraOnGlide(Vec3d oldVelocity, CallbackInfoReturnable<Vec3d> cir) {
        if (Unbreakable.CONFIG.dynamicDamage.ELYTRA()) {
            float multiplier = Unbreakable.CONFIG.dynamicDamage.ELYTRA_MULTIPLIER();
            Vec3d velocity = cir.getReturnValue();
            double x = Math.max(velocity.getX(), velocity.getX() * -2);
            double y = velocity.getY();
            double z = Math.max(velocity.getZ(), velocity.getZ() * -2);
            cachedDamage = Math.max((int) (((x + z) * multiplier) + (y * multiplier)), 0);
        }
    }

    @ModifyConstant(method = "tickGliding", constant = @Constant(intValue = 1, ordinal = 1))
    int damageElytraOnGlide(int constant) {
        constant += cachedDamage;
        cachedDamage = 0;
        return constant;
    }
}
