package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyReturnValue(
            method = "getTridentSpinAttackStrength",
            at = @At("RETURN")
    )
    private static float unbreakable$applyRiptideShatterPenalty(float original, ItemStack stack) {
        if (!ItemShatterHelper.isShattered(stack)
                || !Unbreakable.CONFIG.shatterPenalties.RIPTIDE()) {
            return original;
        }

        return original * ItemShatterHelper.calculateShatterPenalty(stack);
    }
}