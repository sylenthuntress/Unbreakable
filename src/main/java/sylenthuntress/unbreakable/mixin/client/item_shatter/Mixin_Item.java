package sylenthuntress.unbreakable.mixin.client.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.ShatterHelper;


@Mixin(Item.class)
public abstract class Mixin_Item implements ComponentHolder {
    @ModifyReturnValue(
            method = "getItemBarStep",
            at = @At("RETURN")
    )
    private int unbreakable$getItemBarStepWhenShattered(int itemBarStep, ItemStack stack) {
        if (stack.getDamage() <= stack.getMaxDamage()) {
            return itemBarStep;
        }


        if (ShatterHelper.isShattered(stack)) {
            itemBarStep = (int) 13.0F * stack.getOrDefault(
                    UnbreakableComponents.SHATTER_LEVEL,
                    0
            ) / ShatterHelper.getMaxShatterLevel(stack);

            int oneStep = (int) 13.0F / ShatterHelper.getMaxShatterLevel(stack);

            itemBarStep -= MathHelper.clamp(oneStep - Math.round(
                    (float) oneStep
                            - (float) stack.getDamage()
                            * (float) oneStep
                            / (float) stack.getMaxDamage()
            ) * -2, 0, oneStep);
        } else {
            itemBarStep *= -2;
        }

        return Math.min(itemBarStep, 13);
    }

    @ModifyReturnValue(
            method = "getItemBarColor",
            at = @At("RETURN")
    )
    private int unbreakable$getItemBarColorWhenShattered(int itemBarColor, ItemStack stack) {
        if (stack.getDamage() > stack.getMaxDamage()) {
            itemBarColor = Unbreakable.CONFIG.shatteredItemBarColor().rgb();
        }

        return itemBarColor;
    }
}