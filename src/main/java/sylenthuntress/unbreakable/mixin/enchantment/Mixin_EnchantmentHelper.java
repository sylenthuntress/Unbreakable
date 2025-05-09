package sylenthuntress.unbreakable.mixin.enchantment;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.util.ShatterHelper;

@Mixin(EnchantmentHelper.class)
public class Mixin_EnchantmentHelper {
    @ModifyReturnValue(
            method = "hasAnyEnchantmentsWith",
            at = @At("RETURN")
    )
    private static boolean unbreakable$disableBindingWhenShattered(boolean original, ItemStack itemStack, ComponentType<?> componentType) {
        boolean componentIsBinding = componentType == EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE;
        boolean stackIsShattered = ShatterHelper.isShattered(itemStack);
        boolean configEnabled = Unbreakable.CONFIG.disableBindingWhenShattered();

        return original && componentIsBinding && stackIsShattered && configEnabled;
    }
}
