package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.ShatterHelper;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class Mixin_LivingEntity extends Entity {
    public Mixin_LivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(
            method = "canGlideWith",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;willBreakNextUse()Z"
            )
    )
    private static boolean unbreakable$preventUseWhenShattered(boolean original, ItemStack stack) {
        return original && !ShatterHelper.shouldPreventUse(stack);
    }

    @Shadow
    @NotNull
    public abstract ItemStack getWeaponStack();

    @Shadow
    public abstract ItemStack getBlockingItem();

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Unique
    ItemStack sharedItemStack;

    @ModifyExpressionValue(
            method = "blockedByShield",
            at = @At(
                    value = "CONSTANT",
                    args = {
                            "floatValue=0.0F",
                            "ordinal=2"
                    }
            )
    )
    private float unbreakable$applyShieldArcShatterPenalty(float original) {
        final ItemStack stack = this.getBlockingItem();

        if (!ShatterHelper.isShattered(stack)) {
            return original;
        } else if (ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())) {
            return original;
        } else if (!Unbreakable.CONFIG.shatterPenalties.SHIELD_ARC()) {
            return original;
        }

        return original + -1 * (1 - ShatterHelper.calculateShatterPenalty(stack));
    }

    @ModifyExpressionValue(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getEffectiveGravity()D"
            )
    )
    private double unbreakable$applyGlidingGravityShatterPenalty(double original) {
        if (!Unbreakable.CONFIG.shatterPenalties.GLIDING_VELOCITY()) {
            return original;
        }

        double penaltyMultiplier = 1;
        int shatterLevelRecord = 0;

        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            final ItemStack stack = this.getEquippedStack(slot);

            if (!LivingEntity.canGlideWith(stack, slot)
                    || stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0) <= shatterLevelRecord) {
                continue;
            }

            shatterLevelRecord = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
            penaltyMultiplier = 3 - (ShatterHelper.calculateShatterPenalty(stack) * 2);
        }

        return original * penaltyMultiplier;
    }

    @ModifyExpressionValue(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;horizontalLength()D"
            )
    )
    private double unbreakable$applyGlidingVelocityShatterPenalty(double original) {
        if (!Unbreakable.CONFIG.shatterPenalties.GLIDING_VELOCITY()) {
            return original;
        }

        double penaltyMultiplier = 1;
        int shatterLevelRecord = 0;

        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            final ItemStack stack = this.getEquippedStack(slot);

            if (!LivingEntity.canGlideWith(stack, slot)) {
                continue;
            } else if (stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0) <= shatterLevelRecord) {
                continue;
            }

            shatterLevelRecord = stack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
            penaltyMultiplier = Math.max(ShatterHelper.calculateShatterPenalty(stack), 0.1);
        }

        return original * penaltyMultiplier;
    }

    @Shadow
    public abstract double getAttributeBaseValue(RegistryEntry<EntityAttribute> attribute);

    @Inject(
            method = "getEquipmentChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;applyAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"
            )
    )
    private void unbreakable$saveItemStack(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, @Local ItemStack itemStack) {
        sharedItemStack = itemStack;
    }

    @Inject(
            method = "method_61423",
            at = @At("HEAD")
    )
    private void unbreakable$applyAttributeShatterPenalty(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier,
                                                          CallbackInfo ci,
                                                          @Local(argsOnly = true) LocalRef<EntityAttributeModifier> newModifier) {
        final ItemStack stack = sharedItemStack;
        if (stack == null || !ShatterHelper.isShattered(stack)) {
            return;
        }

        double penaltyMultiplier = ShatterHelper.calculateShatterPenalty(stack);
        double newModifierValue;

        if (modifier.value() >= 0) {
            newModifierValue = modifier.value() * penaltyMultiplier;

            newModifier.set(new EntityAttributeModifier(modifier.id(), newModifierValue, modifier.operation())
            );
        } else {
            newModifierValue = modifier.value() + this.getAttributeBaseValue(attribute);
            newModifierValue = newModifierValue - (newModifierValue * penaltyMultiplier);

            newModifier.set(new EntityAttributeModifier(modifier.id(), modifier.value() - newModifierValue, modifier.operation()));
        }
    }
}