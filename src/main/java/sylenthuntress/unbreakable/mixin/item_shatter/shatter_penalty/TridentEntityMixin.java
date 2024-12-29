package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.util.DataTagKeys.SHATTER_BLACKLIST;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends Entity {
    public TridentEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getWeaponStack();

    @ModifyVariable(method = "onEntityHit", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private float onEntityHit$applyShatterPenalty(float original) {
        ItemStack stack = this.getWeaponStack();
        float penaltyMultiplier = 1;
        if (stack != null && ItemShatterHelper.isShattered(stack) && !stack.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.ATTACK_DAMAGE()) {
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original * penaltyMultiplier;
    }
}
