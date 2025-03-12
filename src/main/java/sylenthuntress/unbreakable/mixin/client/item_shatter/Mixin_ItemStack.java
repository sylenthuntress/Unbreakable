package sylenthuntress.unbreakable.mixin.client.item_shatter;

import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.util.ShatterHelper;

import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack implements ComponentHolder {
    @Unique
    private RegistryEntry<EntityAttribute> unbreakable$savedTooltipAttribute;

    // Updates the tooltip for shattered items
    @Inject(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "HEAD"
            ))
    private void unbreakable$saveTooltipAttribute(
            Consumer<Text> textConsumer,
            PlayerEntity player,
            RegistryEntry<EntityAttribute> attribute,
            EntityAttributeModifier modifier,
            CallbackInfo ci) {
        this.unbreakable$savedTooltipAttribute = attribute;
    }

    @ModifyVariable(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private double unbreakable$updateAttributeTooltip(double original) {
        final ItemStack stack = (ItemStack) (Object) this;

        if ((original >= 0)
                || unbreakable$savedTooltipAttribute == EntityAttributes.ATTACK_DAMAGE
                || unbreakable$savedTooltipAttribute == EntityAttributes.ATTACK_SPEED
                || unbreakable$savedTooltipAttribute != EntityAttributes.ARMOR
                || !Unbreakable.CONFIG.shatterPenalties.ARMOR()
                && (unbreakable$savedTooltipAttribute != EntityAttributes.ARMOR_TOUGHNESS
                || !Unbreakable.CONFIG.shatterPenalties.ARMOR_TOUGHNESS())
                && (unbreakable$savedTooltipAttribute != EntityAttributes.KNOCKBACK_RESISTANCE
                || !Unbreakable.CONFIG.shatterPenalties.KNOCKBACK_RESISTANCE())) {
            return original;
        }

        return original * ShatterHelper.calculateShatterPenalty(stack);
    }

    @ModifyVariable(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "STORE",
                    ordinal = 1
            ),
            ordinal = 0
    )
    private double unbreakable$updateAttackDamageTooltip(double original) {
        final ItemStack stack = (ItemStack) (Object) this;

        if (!Unbreakable.CONFIG.shatterPenalties.ATTACK_DAMAGE()) {
            return original;
        }

        return original * ShatterHelper.calculateShatterPenalty(stack);
    }

    @ModifyVariable(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "STORE",
                    ordinal = 2
            ),
            ordinal = 0
    )
    private double unbreakable$updateAttackSpeedTooltip(double original) {
        final ItemStack stack = (ItemStack) (Object) this;

        if (!Unbreakable.CONFIG.shatterPenalties.ATTACK_SPEED()) {
            return original;
        }

        return original * ShatterHelper.calculateShatterPenalty(stack);
    }
}