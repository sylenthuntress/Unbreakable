package sylenthuntress.unbreakable.mixin.durability;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
    private int maxDamageMultiplier(int original) {
        if (!(Unbreakable.CONFIG.onlyMultiplyShatterableItems() && this.isIn(SHATTER_BLACKLIST)))
            return (int) (original * Unbreakable.CONFIG.maxDamageMultiplier());
        return original;
    }
}
