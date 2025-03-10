package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;


@Mixin(ProjectileEntity.class)
public abstract class Mixin_ProjectileEntity extends Entity {
    @Unique
    private static ItemStack savedProjectileStack;

    public Mixin_ProjectileEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "spawnWithVelocity*",
            at = @At("TAIL")
    )
    private static <T extends ProjectileEntity> void setSavedProjectileStack(
            T projectile,
            ServerWorld world,
            ItemStack projectileStack,
            double velocityX,
            double velocityY,
            double velocityZ,
            float power,
            float divergence,
            CallbackInfoReturnable<T> cir) {
        savedProjectileStack = projectileStack;
    }

    @ModifyArgs(
            method = "setVelocity(DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileEntity;calculateVelocity(DDDFF)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    public void unbreakable$applyProjectileShatterPenalty(Args args) {
        final ItemStack stack = savedProjectileStack;

        if (stack == null
                || !ShatterHelper.isShattered(stack)
                || ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())
                || !Unbreakable.CONFIG.shatterPenalties.PROJECTILES()) {
            return;
        }

        final float penaltyMultiplier = ShatterHelper.calculateShatterPenalty(stack);

        double originalVelocity = args.get(0);
        args.set(0, originalVelocity * penaltyMultiplier);

        originalVelocity = args.get(1);
        args.set(1, originalVelocity * penaltyMultiplier);

        originalVelocity = args.get(2);
        args.set(2, originalVelocity * penaltyMultiplier);

        final float originalPower = args.get(3);
        args.set(3, originalPower * penaltyMultiplier);
    }
}
