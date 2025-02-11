package sylenthuntress.unbreakable.mixin.item_repair;

import net.minecraft.component.type.RepairableComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.util.RepairHelper;

@Mixin(RepairableComponent.class)
public abstract class RepairableComponentMixin {
    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void unbreakable$registerRepairMaterials(RegistryEntryList<Item> items, CallbackInfo ci) {
        RepairHelper.addRepairMaterial(items);
    }
}
