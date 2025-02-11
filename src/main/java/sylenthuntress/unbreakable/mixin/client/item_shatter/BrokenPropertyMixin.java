package sylenthuntress.unbreakable.mixin.client.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.item.property.bool.BrokenProperty;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;


@Mixin(BrokenProperty.class)
public class BrokenPropertyMixin {
    @ModifyExpressionValue(
            method = "getValue",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;willBreakNextUse()Z"
            )
    )
    private boolean unbreakable$willBreakNextUse$preventItemBreak(boolean original, ItemStack stack) {
        return original || ItemShatterHelper.isShattered(stack);
    }
}