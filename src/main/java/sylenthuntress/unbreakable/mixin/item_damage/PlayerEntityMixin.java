package sylenthuntress.unbreakable.mixin.item_damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.access.ItemStackAccess;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Unique
    boolean doBonusKnockback;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public boolean unbreakable$incrementedShatterLevel(ItemStack instance) {
        return ((ItemStackAccess) (Object) instance).unbreakable$incrementedShatterLevel();
    }

    @Shadow
    public abstract @NotNull ItemStack getWeaponStack();

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V", ordinal = 0))
    void damageItemOnAttack(Entity target, CallbackInfo ci, @Local(ordinal = 0) float damage, @Local(ordinal = 0) LocalFloatRef newDamage, @Local(ordinal = 1) float bonusDamage, @Local(ordinal = 2) float cooldownProgress, @Local DamageSource damageSource) {
        if (!target.isInvulnerable() && target instanceof LivingEntity livingEntity && livingEntity.hurtTime == 0) {
            ItemStack stack = getWeaponStack();
            if (stack.isDamageable() && Unbreakable.CONFIG.dynamicDamage.COMBAT()) {
                damage += bonusDamage;
                damage *= Math.max(1, 1 + (livingEntity.getArmor() * 0.1F));
                damage += stack.getItem().getBonusAttackDamage(target, damage, damageSource);
                boolean isCrit = cooldownProgress > 0.95
                        && this.fallDistance > 0.0F
                        && !this.isOnGround()
                        && !this.isClimbing()
                        && !this.isTouchingWater()
                        && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                        && !this.hasVehicle()
                        && !this.isSprinting();
                if (isCrit) damage *= 1.5F;
                else if (cooldownProgress <= 0.95)
                    damage += (float) (this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) * (5.0F - (cooldownProgress * 4.0F)));
                stack.damage((int) ((damage * 0.3F) * Unbreakable.CONFIG.dynamicDamage.COMBAT_MULTIPLIER()), (PlayerEntity) (Object) this);
            stack.set(DataComponentTypes.DAMAGE, stack.getOrDefault(DataComponentTypes.DAMAGE, 1) + 1);
            if ((((unbreakable$incrementedShatterLevel(stack)) && stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) == 1) || (stack.shouldBreak() || stack.isEmpty())) && Unbreakable.CONFIG.bonusDamageOnBreak.DO_BONUS()) {
                newDamage.set(newDamage.get() * Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_ATTACK_MULTIPLIER());
                doBonusKnockback = true;
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 1F, 0.5F);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, this.getSoundCategory(), 0.3F, 2F);
            } else if (stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) == 0) {
                stack.set(DataComponentTypes.DAMAGE, stack.getOrDefault(DataComponentTypes.DAMAGE, 1) - 1);
            }
            }
        }
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getKnockbackAgainst(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)F"))
    float doBonusKnockback(float original) {
        if (doBonusKnockback) original += Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_KNOCKBACK();
        doBonusKnockback = false;
        return original;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 1))
    void damageItemOnSweep(Entity target, CallbackInfo ci, @Local(ordinal = 7) float damage) {
        if (!target.isInvulnerable() && target instanceof LivingEntity livingEntity) {
            damage *= Math.max(1, 1 + (livingEntity.getArmor() * 0.1F));
            getWeaponStack().damage((int) damage / 4, (PlayerEntity) (Object) this);
        }
    }
}
