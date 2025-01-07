package sylenthuntress.unbreakable.mixin.item_damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Unique
    float projectilePower;

    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @ModifyArgs(method = "setVelocity(DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;calculateVelocity(DDDFF)Lnet/minecraft/util/math/Vec3d;"))
    public void setProjectilePower(Args args) {
        projectilePower = args.get(3);
    }

    @Inject(method = "triggerProjectileSpawned", at = @At("TAIL"))
    public void damageItemOnShoot(ServerWorld world, ItemStack projectileStack, CallbackInfo ci) {
        if (Unbreakable.CONFIG.dynamicDamage.PROJECTILE() && this.getOwner() instanceof PlayerEntity player) {
            ItemStack projectileItemStack = player.getActiveItem();
            projectileItemStack.damage((int) (Math.ceil(projectilePower * 2) * Unbreakable.CONFIG.dynamicDamage.PROJECTILE_MULTIPLIER()), player);
        }
    }
}
