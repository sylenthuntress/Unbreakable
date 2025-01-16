package sylenthuntress.unbreakable.mixin.client.item_repair.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.util.Unbreakable;


@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    // Disables the "Too Expensive!" text in anvils
    @ModifyExpressionValue(method = "drawForeground", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z"))
    private boolean disableTooExpensiveClient(boolean original) {
        return !Unbreakable.CONFIG.anvilRepair.TOO_EXPENSIVE();
    }
}