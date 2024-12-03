package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
    @Unique
    ItemStack savedItemStack;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;IILnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;refreshPositionAndAngles(DDDFF)V"))
    private void setSavedItemStack(PlayerEntity thrower, World world, int luckBonus, int waitTimeReductionTicks, ItemStack stack, CallbackInfo ci) {
        savedItemStack = stack;
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;IILnet/minecraft/item/ItemStack;)V", at = @At("STORE"))
    private Vec3d FishingBobberEntity$applyShatterPenalty(Vec3d vec3d) {
        ItemStack stack = savedItemStack;
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
