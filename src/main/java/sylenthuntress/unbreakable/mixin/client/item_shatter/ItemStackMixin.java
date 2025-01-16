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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.function.Consumer;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @Unique
    private RegistryEntry<EntityAttribute> savedTooltipAttribute;

    @Shadow
    public abstract ItemStack copy();

    // Updates the tooltip for shattered items
    @Inject(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "HEAD"
            ))
    private void saveTooltipAttribute(Consumer<Text> textConsumer, PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier, CallbackInfo ci) {
        this.savedTooltipAttribute = attribute;
    }

    @ModifyVariable(method = "appendAttributeModifierTooltip", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private double updateAttributeTooltip(double original) {
        ItemStack stack = this.copy();
        double penaltyMultiplier = 1;
        if ((original >= 0 && (savedTooltipAttribute != EntityAttributes.ATTACK_DAMAGE && savedTooltipAttribute != EntityAttributes.ATTACK_SPEED)) && (savedTooltipAttribute == EntityAttributes.ARMOR && Unbreakable.CONFIG.shatterPenalties.ARMOR()) || (savedTooltipAttribute == EntityAttributes.ARMOR_TOUGHNESS && Unbreakable.CONFIG.shatterPenalties.ARMOR_TOUGHNESS()) || (savedTooltipAttribute == EntityAttributes.KNOCKBACK_RESISTANCE && Unbreakable.CONFIG.shatterPenalties.KNOCKBACK_RESISTANCE())) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original * penaltyMultiplier;
    }

    @ModifyVariable(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "STORE",
                    ordinal = 1
            ),
            ordinal = 0
    )
    private double updateAttackDamageTooltip(double original) {
        ItemStack stack = this.copy();
        double penaltyMultiplier = 1;
        if (Unbreakable.CONFIG.shatterPenalties.ATTACK_DAMAGE()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original * penaltyMultiplier;
    }

    @ModifyVariable(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "STORE",
                    ordinal = 2
            ),
            ordinal = 0
    )
    private double updateAttackSpeedTooltip(double original) {
        ItemStack stack = this.copy();
        double penaltyMultiplier = 1;
        if (Unbreakable.CONFIG.shatterPenalties.ATTACK_SPEED()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original * penaltyMultiplier;
    }
}