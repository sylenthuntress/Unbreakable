package sylenthuntress.unbreakable.mixin.item_repair;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sylenthuntress.unbreakable.util.RepairMaterialRegistry;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
    @Inject(method = "remove", at = @At("HEAD"))
    private <T> void unregisterRepairMaterials(ComponentType<? extends T> type, CallbackInfoReturnable<T> cir) {
        if (type.equals(DataComponentTypes.REPAIRABLE)) {
            RepairableComponent repairableComponent = this.get(DataComponentTypes.REPAIRABLE);
            RepairMaterialRegistry.getInstance().removeRepairMaterial(repairableComponent.items());
        }
    }

    @Inject(method = "decrement", at = @At("HEAD"))
    private void unregisterRepairMaterials(int amount, CallbackInfo ci) {
        if (this.contains(DataComponentTypes.REPAIRABLE)) {
            RepairableComponent repairableComponent = this.get(DataComponentTypes.REPAIRABLE);
            RepairMaterialRegistry.getInstance().removeRepairMaterial(repairableComponent.items());
        }
    }
}
