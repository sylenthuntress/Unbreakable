package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
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
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public static boolean canGlideWith(ItemStack stack, EquipmentSlot slot) {
        return true;
    }

    @ModifyExpressionValue(method = "canGlideWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;willBreakNextUse()Z"))
    private static boolean canGlideWith$applyShatterPenalty(boolean original, ItemStack stack) {
        return original && !ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack);
    }

    @Shadow
    @NotNull
    public abstract ItemStack getWeaponStack();

    @Shadow
    public abstract ItemStack getBlockingItem();

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    protected abstract boolean isArmorSlot(EquipmentSlot slot);

    @Unique
    ItemStack sharedItemStack;

    @ModifyReturnValue(method = "canEquip", at = @At("RETURN"))
    private boolean canEquip$applyShatterPenalty(boolean original, ItemStack stack) {
        return original && !ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack);
    }

    @ModifyReturnValue(method = "canEquipFromDispenser", at = @At("RETURN"))
    private boolean canEquipFromDispenser$applyShatterPenalty(boolean original, ItemStack stack) {
        return original && !ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack);
    }

    @Inject(method = "damageEquipment", at = @At("TAIL"))
    private void damageEquipment$applyShatterPenalty(DamageSource source, float amount, EquipmentSlot[] slots, CallbackInfo ci) {
        for (EquipmentSlot slot : slots) {
            ItemStack stack = this.getEquippedStack(slot);
            if (isArmorSlot(slot)) continue;
            if (ItemShatterHelper.shouldPreventUse(stack.getRegistryEntry(), stack)) {
                dropStack(Objects.requireNonNull(this.getServer()).getWorld(this.getWorld().getRegistryKey()), stack);
                stack.decrement(stack.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1));
            }
        }
    }

    @ModifyExpressionValue(method = "blockedByShield", at = @At(value = "CONSTANT", args = {"floatValue=0.0F", "ordinal=2"}))
    public float blockedByShield$applyShatterPenalty(float original) {
        ItemStack stack = this.getBlockingItem();
        float penaltyMultiplier = 0;
        if (ItemShatterHelper.isShattered(stack) && !ItemShatterHelper.isInList$shatterBlacklist(stack.getRegistryEntry()) && Unbreakable.CONFIG.shatterPenalties.SHIELD_ARC())
            penaltyMultiplier += 1 - ItemShatterHelper.calculateShatterPenalty(stack);
        return original + (-1 * penaltyMultiplier);
    }

    @ModifyExpressionValue(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEffectiveGravity()D"))
    public double calcGlidingVelocity$gravity$applyShatterPenalty(double original) {
        double penaltyMultiplier = 1;
        int shatterLevelRecord = 0;
        List<EquipmentSlot> list = EquipmentSlot.VALUES.stream().filter((slot) -> canGlideWith(this.getEquippedStack(slot), slot)).toList();
        if (Unbreakable.CONFIG.shatterPenalties.GLIDING_VELOCITY()) for (EquipmentSlot slot : list) {
            ItemStack stack = this.getEquippedStack(slot);
            if (stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) <= shatterLevelRecord) continue;
            shatterLevelRecord = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            penaltyMultiplier = 3 - (ItemShatterHelper.calculateShatterPenalty(stack) * 2);
        }
        return original * penaltyMultiplier;
    }

    @ModifyExpressionValue(method = "calcGlidingVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;horizontalLength()D"))
    public double calcGlidingVelocity$horizontalLength$applyShatterPenalty(double original) {
        double penaltyMultiplier = 1;
        int shatterLevelRecord = 0;
        List<EquipmentSlot> list = EquipmentSlot.VALUES.stream().filter((slot) -> canGlideWith(this.getEquippedStack(slot), slot)).toList();
        if (Unbreakable.CONFIG.shatterPenalties.GLIDING_VELOCITY()) for (EquipmentSlot slot : list) {
            ItemStack stack = this.getEquippedStack(slot);
            if (stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) <= shatterLevelRecord) continue;
            shatterLevelRecord = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            penaltyMultiplier = Math.max(ItemShatterHelper.calculateShatterPenalty(stack), 0.1);
        }
        return original * penaltyMultiplier;
    }

    @Shadow
    public abstract double getAttributeBaseValue(RegistryEntry<EntityAttribute> attribute);

    @Inject(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;applyAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"))
    void applyShatterPenalty(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, @Local ItemStack itemStack) {
        sharedItemStack = itemStack;
    }

    @Inject(method = "method_61423", at = @At("HEAD"))
    void applyShatterPenalty(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci, @Local(argsOnly = true) LocalRef<EntityAttributeModifier> newModifier) {
        if (sharedItemStack != null) {
            ItemStack itemStack = sharedItemStack;
            if (ItemShatterHelper.isShattered(itemStack)) {
                double penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(itemStack);
                double newModifierValue;
                if (modifier.value() >= 0) {
                    newModifierValue = modifier.value() * penaltyMultiplier;
                    newModifier.set(new EntityAttributeModifier(modifier.id(), newModifierValue, modifier.operation()));
                } else {
                    newModifierValue = modifier.value() + this.getAttributeBaseValue(attribute);
                    newModifierValue = newModifierValue - (newModifierValue * penaltyMultiplier);
                    newModifier.set(new EntityAttributeModifier(modifier.id(), modifier.value() - newModifierValue, modifier.operation()));
                }
            }
        }
    }
}