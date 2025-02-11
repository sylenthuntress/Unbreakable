package sylenthuntress.unbreakable.mixin.item_repair.grindstone;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.access.GrindstoneScreenHandlerAccess;

@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public class GrindstoneScreenHandlerOutputSlotMixin {
    @Shadow
    @Final
    GrindstoneScreenHandler field_16780;

    @Unique
    public int unbreakable$getRepairCost(GrindstoneScreenHandler instance) {
        return ((GrindstoneScreenHandlerAccess) instance).unbreakable$getRepairCost();
    }

    @Inject(
            method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD")
    )
    public void unbreakable$onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!player.isCreative())
            player.addExperienceLevels(-unbreakable$getRepairCost(this.field_16780));
    }
}