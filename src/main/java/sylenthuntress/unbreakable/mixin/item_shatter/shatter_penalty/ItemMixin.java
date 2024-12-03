package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(Item.class)
public abstract class ItemMixin implements ComponentHolder {
    @ModifyReturnValue(method = "getMiningSpeed", at = @At("RETURN"))
    public float getMiningSpeed$applyShatterPenalty(float original, ItemStack stack) {
        if (Unbreakable.CONFIG.shatterPenalties.MINING_SPEED()) {
            return original * ItemShatterHelper.calculateShatterPenalty(stack);
        }
        return original;
    }
}
