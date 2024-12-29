package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

import static sylenthuntress.unbreakable.provider.ModItemTagProvider.SHATTER_BLACKLIST;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    @Shadow
    public abstract ItemStack copy();

    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @ModifyReturnValue(method = "useOnBlock", at = @At(value = "RETURN", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult useOnBlock$applyShatterPenalty1(ActionResult original) {
        if (ItemShatterHelper.isInList$shatterPreventsUse(this.getRegistryEntry(), this.copy()) && this.getOrDefault(ModComponents.SHATTER_LEVEL, 0) > 0)
            return ActionResult.FAIL;
        return original;
    }

    @ModifyReturnValue(method = "useOnEntity", at = @At(value = "RETURN", target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult useOnEntity$applyShatterPenalty(ActionResult original) {
        if (ItemShatterHelper.isInList$shatterPreventsUse(this.getRegistryEntry(), this.copy()) && this.getOrDefault(ModComponents.SHATTER_LEVEL, 0) > 0)
            return ActionResult.FAIL;
        return original;
    }

    @ModifyExpressionValue(method = "calculateDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getItemDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;I)I"))
    private int applyShatterPenalty(int original) {
        double penaltyMultiplier = 1;
        if (ItemShatterHelper.isShattered((ItemStack) (Object) this) && !this.isIn(SHATTER_BLACKLIST) && Unbreakable.CONFIG.shatterPenalties.DURABILITY_LOSS())
            penaltyMultiplier = 2 - ItemShatterHelper.calculateShatterPenalty((ItemStack) (Object) this);
        return (int) (original * penaltyMultiplier);
    }
}
