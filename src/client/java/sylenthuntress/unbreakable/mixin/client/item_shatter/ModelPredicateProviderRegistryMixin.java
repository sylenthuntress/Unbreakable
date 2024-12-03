package sylenthuntress.unbreakable.mixin.client.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ModComponents;

@Environment(EnvType.CLIENT)
@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {
    @ModifyExpressionValue(method = "method_27884", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;willBreakNextUse()Z"))
    private static boolean willBreakNextUse$preventItemBreak(boolean original, ItemStack stack) {
        return original || stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0) > 0;
    }
}