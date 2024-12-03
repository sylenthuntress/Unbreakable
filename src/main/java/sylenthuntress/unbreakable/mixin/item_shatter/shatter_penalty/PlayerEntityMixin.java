package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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

    @ModifyConstant(method = "updateTurtleHelmet", constant = @Constant(intValue = 200, ordinal = 0))
    int applyShatterPenalty(int constant) {
        ItemStack stack = getEquippedStack(EquipmentSlot.HEAD);
        double penaltyMultiplier = 1;
        if (ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.ARMOR_EFFECTS())
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        return (int) (constant * penaltyMultiplier);
    }

}
