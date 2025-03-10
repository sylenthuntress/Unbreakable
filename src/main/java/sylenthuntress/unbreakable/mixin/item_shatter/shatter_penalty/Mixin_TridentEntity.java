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
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.util.ShatterHelper;


@Mixin(TridentEntity.class)
public abstract class Mixin_TridentEntity extends Entity {
    public Mixin_TridentEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getWeaponStack();

    @ModifyVariable(
            method = "onEntityHit",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private float unbreakable$applyShatterDamagePenalty(float original) {
        ItemStack stack = this.getWeaponStack();

        if (stack == null
                || !ShatterHelper.isShattered(stack)
                || ConfigHelper.isInList$shatterBlacklist(stack.getRegistryEntry())
                || !Unbreakable.CONFIG.shatterPenalties.ATTACK_DAMAGE()) {
            return original;
        }

        return original * ShatterHelper.calculateShatterPenalty(stack);
    }
}
