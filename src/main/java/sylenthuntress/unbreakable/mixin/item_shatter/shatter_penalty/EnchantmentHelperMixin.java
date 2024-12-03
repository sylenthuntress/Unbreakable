package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getTridentSpinAttackStrength", at = @At("RETURN"))
    private static float getTridentSpinAttackStrength$applyShatterPenalty(float original, ItemStack riptideStack) {
        ItemStack stack = riptideStack.copy();
        float penaltyMultiplier = 1;
        if (ItemShatterHelper.isShattered(stack) && Unbreakable.CONFIG.shatterPenalties.RIPTIDE()) {
            int enchantmentUnbreakingLevel = ItemShatterHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            stack.set(ModComponents.MAX_SHATTER_LEVEL, stack.getOrDefault(ModComponents.MAX_SHATTER_LEVEL, Unbreakable.CONFIG.maxShatterLevel() + (enchantmentUnbreakingLevel * Unbreakable.CONFIG.enchantmentScaling())) + 2);
            penaltyMultiplier = ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original * penaltyMultiplier;
    }
}