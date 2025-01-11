package sylenthuntress.unbreakable.mixin.client.item_repair.grindstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.access.SmithingScreenHandlerAccess;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin extends ForgingScreen<SmithingScreenHandler> {

    public SmithingScreenMixin(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Unique
    public int unbreakable$getRepairCost(SmithingScreenHandler instance) {
        return ((SmithingScreenHandlerAccess) instance).unbreakable$getRepairCost();
    }

    @WrapOperation(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;FFFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V"))
    private void cancelArmorStandWhenRepairing(DrawContext context, float x, float y, float size, Vector3f vector3f, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, Operation<Void> original) {
        if (unbreakable$getRepairCost(this.getScreenHandler()) == 0)
            original.call(context, x, y, size, vector3f, quaternionf, quaternionf2, entity);
    }
}
