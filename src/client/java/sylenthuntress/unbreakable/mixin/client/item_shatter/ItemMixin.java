package sylenthuntress.unbreakable.mixin.client.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public abstract class ItemMixin implements ComponentHolder {
    @ModifyReturnValue(method = "getItemBarStep", at = @At("RETURN"))
    private int getItemBarStepWhenShattered(int itemBarStep, ItemStack stack) {
        if (stack.getDamage() > stack.getMaxDamage()) {
            if (ItemShatterHelper.isShattered(stack)) {
                itemBarStep = (int) 13.0F * stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) / ItemShatterHelper.getMaxShatterLevel(stack);
                int oneStep = (int) 13.0F / ItemShatterHelper.getMaxShatterLevel(stack);
                itemBarStep -= MathHelper.clamp(oneStep - Math.round(
                        (float) oneStep - (float) stack.getDamage() * (float) oneStep / (float) stack.getMaxDamage()
                ) * -2, 0, oneStep);
            } else {
                itemBarStep *= -2;
            }
        }
        return itemBarStep;
    }

    @ModifyReturnValue(method = "getItemBarColor", at = @At("RETURN"))
    private int getItemBarColorWhenShattered(int itemBarColor, ItemStack stack) {
        if (stack.getDamage() > stack.getMaxDamage()) itemBarColor = Unbreakable.CONFIG.shatteredItemBarColor().rgb();
        return itemBarColor;
    }
}