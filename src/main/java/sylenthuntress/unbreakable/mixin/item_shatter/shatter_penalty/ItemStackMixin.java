package sylenthuntress.unbreakable.mixin.item_shatter.shatter_penalty;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Shadow
    public abstract Item getItem();

    @ModifyReturnValue(
            method = "useOnBlock",
            at = @At(
                    value = "RETURN",
                    target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
            )
    )
    private ActionResult unbreakable$preventBlockUseWhenShattered(ActionResult original) {
        if (ItemShatterHelper.shouldPreventUse((ItemStack) (Object) this)) {
            return ActionResult.FAIL;
        }

        return original;
    }

    @ModifyReturnValue(
            method = "useOnEntity",
            at = @At(
                    value = "RETURN",
                    target = "Lnet/minecraft/item/Item;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
            )
    )
    private ActionResult unbreakable$preventEntityUseWhenShattered(ActionResult original) {
        if (ItemShatterHelper.shouldPreventUse((ItemStack) (Object) this)) {
            return ActionResult.FAIL;
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "calculateDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getItemDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;I)I"
            )
    )
    private int unbreakable$applyDurabilityShatterPenalty(int original) {
        final ItemStack stack = (ItemStack) (Object) this;

        if (!ItemShatterHelper.isShattered(stack)) {
            return original;
        }

        return (int) Math.ceil(original * (2 - ItemShatterHelper.calculateShatterPenalty(stack)));
    }
}
