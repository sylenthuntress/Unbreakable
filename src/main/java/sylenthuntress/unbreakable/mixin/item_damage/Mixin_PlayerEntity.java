package sylenthuntress.unbreakable.mixin.item_damage;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.util.ShatterHelper;

@Mixin(PlayerEntity.class)
public abstract class Mixin_PlayerEntity extends LivingEntity {
    @Unique
    boolean unbreakable$doBonus;

    protected Mixin_PlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract @NotNull ItemStack getWeaponStack();

    @Shadow
    public abstract SoundCategory getSoundCategory();

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;sidedDamage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    private boolean unbreakable$dynamicItemDamageOnAttack(
            Entity instanceTarget,
            DamageSource source,
            float amount,
            Operation<Boolean> original) {
        unbreakable$doBonus = false;
        final PlayerEntity attacker = (PlayerEntity) (Object) this;
        final ItemStack stack = getWeaponStack();

        if (stack.isDamageable() && instanceTarget instanceof LivingEntity livingTarget && livingTarget.canTakeDamage()) {
            float itemDamage = Unbreakable.CONFIG.dynamicDamage.COMBAT()
                    ? amount * Math.max(
                    1, 1 + (livingTarget.getArmor() * 0.1F))
                    : 0;

            if (Unbreakable.CONFIG.bonusDamageOnBreak.DO_BONUS() && !ShatterHelper.isShattered(stack)) {
                ItemStack dummyStack = stack.copy();
                dummyStack.damage(Math.round(itemDamage), attacker);
                dummyStack.postDamageEntity(livingTarget, attacker);

                if (ShatterHelper.isShattered(dummyStack)) {
                    unbreakable$doBonus = true;
                }
            }

            stack.damage(Math.round(itemDamage), attacker, EquipmentSlot.MAINHAND);
        }

        if (unbreakable$doBonus) {
            amount *= Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_ATTACK_MULTIPLIER();

            this.getWorld().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.BLOCK_GLASS_BREAK,
                    this.getSoundCategory(),
                    amount,
                    0.5F
            );
            this.getWorld().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.BLOCK_ANVIL_PLACE,
                    this.getSoundCategory(),
                    amount * -0.7F,
                    2F
            );
        }

        return original.call(instanceTarget, source, amount);
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getKnockbackAgainst(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)F"
            )
    )
    private float unbreakable$doBonusKnockback(float original) {
        return unbreakable$doBonus
                ? original + Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_KNOCKBACK()
                : original;
    }

    @WrapOperation(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;serverDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"
            )
    )
    private void unbreakable$doBonusOnSweep(
            LivingEntity livingTarget,
            DamageSource source,
            float amount,
            Operation<Void> original) {

        if (unbreakable$doBonus) {
            livingTarget.takeKnockback(
                    0.4F * Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_KNOCKBACK(),
                    MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                    -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0))
            );
            amount *= Unbreakable.CONFIG.bonusDamageOnBreak.BONUS_ATTACK_MULTIPLIER();
        }

        original.call(livingTarget, source, amount);
    }

    @Unique
    private int unbreakable$lastAgeSignature = 0;

    @WrapMethod(
            method = "damageShield"
    )
    private void unbreakable$preventShieldDamageSpam(float amount, Operation<Void> original) {
        if (!Unbreakable.CONFIG.damageShieldCooldown()
                || unbreakable$lastAgeSignature == 0
                || this.age > unbreakable$lastAgeSignature + 10) {
            original.call(amount);
            unbreakable$lastAgeSignature = this.age;
        }
    }
}
