package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.List;
import java.util.Objects;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

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
        return original && !ItemShatterHelper.isInList$shatterPreventsUse(stack.getRegistryEntry(), stack);
    }

    @Shadow
    public abstract Iterable<ItemStack> getEquippedItems();

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    @NotNull
    public abstract ItemStack getWeaponStack();

    @Shadow
    public abstract ItemStack getBlockingItem();

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    protected abstract boolean isArmorSlot(EquipmentSlot slot);

    @ModifyReturnValue(method = "getAttributeValue", at = @At(value = "RETURN"))
    public double getAttributeValue$applyShatterPenalty(double original, RegistryEntry<EntityAttribute> attribute) {
        if ((attribute == EntityAttributes.ARMOR && Unbreakable.CONFIG.shatterPenalties.ARMOR()) || (attribute == EntityAttributes.ARMOR_TOUGHNESS && Unbreakable.CONFIG.shatterPenalties.ARMOR_TOUGHNESS()) || (attribute == EntityAttributes.KNOCKBACK_RESISTANCE && Unbreakable.CONFIG.shatterPenalties.KNOCKBACK_RESISTANCE())) {
            double newAttribute = 0;
            for (ItemStack stack : this.getEquippedItems()) {
                double itemAttribute = ItemShatterHelper.getAttribute(stack, attribute, this.getAttributes().getBaseValue(attribute));
                if (itemAttribute >= 0)
                    newAttribute += itemAttribute * ItemShatterHelper.calculateShatterPenalty(stack);
            }
            original = newAttribute;
        }
        if ((attribute == EntityAttributes.ATTACK_DAMAGE && Unbreakable.CONFIG.shatterPenalties.ATTACK_DAMAGE()) || (attribute == EntityAttributes.ATTACK_SPEED && Unbreakable.CONFIG.shatterPenalties.ATTACK_SPEED())) {
            ItemStack stack = this.getWeaponStack();
            double itemAttribute = ItemShatterHelper.getAttribute(stack, attribute, this.getAttributes().getBaseValue(attribute));
            if (itemAttribute != this.getAttributes().getBaseValue(attribute))
                original = itemAttribute * ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original;
    }

    @ModifyReturnValue(method = "canEquip", at = @At("RETURN"))
    private boolean canEquip$applyShatterPenalty(boolean original, ItemStack stack) {
        return original && !ItemShatterHelper.isInList$shatterPreventsUse(stack.getRegistryEntry(), stack);
    }

    @ModifyReturnValue(method = "canEquipFromDispenser", at = @At("RETURN"))
    private boolean canEquipFromDispenser$applyShatterPenalty(boolean original, ItemStack stack) {
        return original && !ItemShatterHelper.isInList$shatterPreventsUse(stack.getRegistryEntry(), stack);
    }

    @Inject(method = "damageEquipment", at = @At("TAIL"))
    private void damageEquipment$applyShatterPenalty(DamageSource source, float amount, EquipmentSlot[] slots, CallbackInfo ci) {
        for (EquipmentSlot slot : slots) {
            ItemStack stack = this.getEquippedStack(slot);
            if (isArmorSlot(slot)) continue;
            if (ItemShatterHelper.isInList$shatterPreventsUse(stack.getRegistryEntry(), stack)) {
                dropStack(Objects.requireNonNull(this.getServer()).getWorld(this.getWorld().getRegistryKey()), stack);
                stack.decrement(stack.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1));
            }
        }
    }

    @ModifyExpressionValue(method = "blockedByShield", at = @At(value = "CONSTANT", args = {"floatValue=0.0F", "ordinal=2"}))
    public float blockedByShield$applyShatterPenalty(float original) {
        ItemStack stack = this.getBlockingItem();
        float penaltyMultiplier = 0;
        if (ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.SHIELD_ARC())
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
}