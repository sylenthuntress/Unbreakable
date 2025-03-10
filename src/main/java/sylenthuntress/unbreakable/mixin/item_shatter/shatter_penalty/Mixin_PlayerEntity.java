package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;


@Mixin(PlayerEntity.class)
public abstract class Mixin_PlayerEntity extends Entity {

    public Mixin_PlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract @NotNull ItemStack getWeaponStack();

    @ModifyExpressionValue(
            method = "updateTurtleHelmet",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=200")
    )
    int unbreakable$applyArmorEffectsShatterPenalty(int original) {
        ItemStack stack = getEquippedStack(EquipmentSlot.HEAD);

        if (!ShatterHelper.isShattered(stack)) {
            return original;
        } else if (ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())) {
            return original;
        } else if (!Unbreakable.CONFIG.shatterPenalties.ARMOR_EFFECTS()) {
            return original;
        }

        return Math.round(original * ShatterHelper.calculateShatterPenalty(stack));
    }

}
