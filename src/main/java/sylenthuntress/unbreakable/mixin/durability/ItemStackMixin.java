package sylenthuntress.unbreakable.mixin.durability;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.Unbreakable;


@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    @ModifyReturnValue(method = "getMaxDamage", at = @At("RETURN"))
    private int maxDamageMultiplier(int original) {
        if (!ItemShatterHelper.isInList$durabilityModifier(this.getRegistryEntry()))
            return (int) (original * Unbreakable.CONFIG.durabilityModifier.MULTIPLIER());
        return original;
    }
}
