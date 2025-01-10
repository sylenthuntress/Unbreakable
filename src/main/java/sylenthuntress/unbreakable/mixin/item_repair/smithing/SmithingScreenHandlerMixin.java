package sylenthuntress.unbreakable.mixin.item_repair.smithing;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import sylenthuntress.unbreakable.util.RepairMaterialRegistry;

import java.util.function.Predicate;

@Debug(export = true)
@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {
    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairableItemsAsBase(Args args) {
        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or(ItemStack::isDamageable));
    }

    @ModifyArgs(method = "createForgingSlotsManager", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;"))
    private static void allowRepairMaterials(Args args) {
        Predicate<ItemStack> originalPredicate = args.get(3);
        args.set(3, originalPredicate.or((stack) -> RepairMaterialRegistry.getInstance().isRepairMaterial(stack.getItem())));
    }
}