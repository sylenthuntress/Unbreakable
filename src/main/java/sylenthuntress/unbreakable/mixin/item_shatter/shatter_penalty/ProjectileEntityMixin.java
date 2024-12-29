package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.util.DataTagKeys.SHATTER_BLACKLIST;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {
    @Unique
    private static ItemStack savedProjectileStack;

    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;DDDFF)Lnet/minecraft/entity/projectile/ProjectileEntity;", at = @At(value = "TAIL"))
    private static <T extends ProjectileEntity> void setSavedProjectileStack(T projectile, ServerWorld world, ItemStack projectileStack, double velocityX, double velocityY, double velocityZ, float power, float divergence, CallbackInfoReturnable<T> cir) {
        savedProjectileStack = projectileStack;
    }

    @Inject(method = "spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity$ProjectileCreator;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;FFF)Lnet/minecraft/entity/projectile/ProjectileEntity;", at = @At(value = "TAIL"))
    private static <T extends ProjectileEntity> void setSavedProjectileStack(ProjectileEntity.ProjectileCreator<T> creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, float roll, float power, float divergence, CallbackInfoReturnable<T> cir) {
        savedProjectileStack = projectileStack;
    }

    @Inject(method = "spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity$ProjectileCreator;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;DDDFF)Lnet/minecraft/entity/projectile/ProjectileEntity;", at = @At(value = "TAIL"))
    private static <T extends ProjectileEntity> void setSavedProjectileStack(ProjectileEntity.ProjectileCreator<T> creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, double velocityX, double velocityY, double velocityZ, float power, float divergence, CallbackInfoReturnable<T> cir) {
        savedProjectileStack = projectileStack;
    }

    @ModifyArgs(method = "setVelocity(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;calculateVelocity(DDDFF)Lnet/minecraft/util/math/Vec3d;"))
    public void setVelocity$applyShatterPenalty(Args args) {
        float penaltyMultiplier = 1;
        ItemStack stack = savedProjectileStack;
        if (stack != null && ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.PROJECTILES()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        double originalVelocity = args.get(0);
        args.set(0, originalVelocity * penaltyMultiplier);
        originalVelocity = args.get(1);
        args.set(1, originalVelocity * penaltyMultiplier);
        originalVelocity = args.get(2);
        args.set(2, originalVelocity * penaltyMultiplier);
        float originalPower = args.get(3);
        args.set(3, originalPower * penaltyMultiplier);
    }
}
