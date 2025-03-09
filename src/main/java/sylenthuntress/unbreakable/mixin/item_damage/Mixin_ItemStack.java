package sylenthuntress.unbreakable.mixin.item_damage;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.util.ItemShatterHelper;


@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack {
    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    @ModifyReturnValue(
            method = "getMaxDamage",
            at = @At("RETURN")
    )
    private int unbreakable$maxDamageMultiplier(int original) {
        if (ItemShatterHelper.isInList$durabilityModifier(this.getRegistryEntry())) {
            return original;
        }

        return Math.round(original * Unbreakable.CONFIG.durabilityModifier.MULTIPLIER());
    }
}
