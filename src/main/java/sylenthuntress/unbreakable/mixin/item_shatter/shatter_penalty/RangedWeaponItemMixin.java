package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.List;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {
    @Unique
    private ItemStack savedItemStack;

    @Inject(method = "shootAll", at = @At(value = "HEAD"))
    public void setSavedItemStack(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, LivingEntity target, CallbackInfo ci) {
        savedItemStack = stack;
    }

    @ModifyArgs(method = "method_61659", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;IFFFLnet/minecraft/entity/LivingEntity;)V"))
    public void shootAll$applyShatterPenalty(Args args) {
        ItemStack stack = savedItemStack;
        if (ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.PROJECTILES()) {
            // Speed
            float projectileSpeed = args.get(3);
            projectileSpeed *= ItemShatterHelper.calculateShatterPenalty(stack);
            args.set(3, Math.max(0.5F, projectileSpeed));
            // Yaw
            float projectileYaw = args.get(5);
            projectileYaw *= ItemShatterHelper.calculateShatterPenalty(stack);
            args.set(5, Math.max(0.5F, projectileYaw));
        }
    }
}
