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
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow
    public abstract @NotNull ItemStack getWeaponStack();

    @ModifyExpressionValue(method = "updateTurtleHelmet", at = @At(value = "CONSTANT", args = "intValue=200"))
    int applyShatterPenalty(int original) {
        ItemStack stack = getEquippedStack(EquipmentSlot.HEAD);
        double penaltyMultiplier = 1;
        if (ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.ARMOR_EFFECTS())
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        return (int) (original * penaltyMultiplier);
    }

}
