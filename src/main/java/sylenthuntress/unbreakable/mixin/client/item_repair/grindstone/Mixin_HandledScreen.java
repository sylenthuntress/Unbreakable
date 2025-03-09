package sylenthuntress.unbreakable.mixin.client.item_repair.grindstone;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.access.GrindstoneScreenHandlerAccess;
import sylenthuntress.unbreakable.access.SmithingScreenHandlerAccess;

@Mixin(HandledScreen.class)
public abstract class Mixin_HandledScreen {
    @Shadow
    protected int backgroundWidth;

    @Unique
    public int unbreakable$getRepairCost(GrindstoneScreenHandler instance) {
        return ((GrindstoneScreenHandlerAccess) instance).unbreakable$getRepairCost();
    }

    @Unique
    public int unbreakable$getRepairCost(SmithingScreenHandler instance) {
        return ((SmithingScreenHandlerAccess) instance).unbreakable$getRepairCost();
    }

    @SuppressWarnings({"rawtypes", "DataFlowIssue"})
    @Inject(method = "drawForeground", at = @At("TAIL"))
    void unbreakable$drawXpCost(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if ((HandledScreen) (Object) this instanceof GrindstoneScreen grindstoneScreen) {
            int repairCost = unbreakable$getRepairCost(grindstoneScreen.getScreenHandler());

            if (repairCost <= 0) {
                return;
            }

            int textColor = 8453920;
            Text text;
            if (!grindstoneScreen.getScreenHandler().getSlot(0).hasStack())
                text = null;
            else {
                text = Text.translatable("unbreakable.container.repair.levelCost", repairCost);
                if (grindstoneScreen.getScreenHandler().getSlot(2).getStack().isEmpty()) {
                    textColor = 16736352;
                }
            }

            if (text != null) {
                int displayX = this.backgroundWidth - 8 - grindstoneScreen.getTextRenderer().getWidth(text) - 2;
                context.fill(displayX - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
                context.drawTextWithShadow(grindstoneScreen.getTextRenderer(), text, displayX, 69, textColor);
            }
        } else if ((HandledScreen) (Object) this instanceof SmithingScreen smithingScreen) {
            int repairCost = unbreakable$getRepairCost(smithingScreen.getScreenHandler());

            if (repairCost <= 0) {
                return;
            }

            int textColor = 8453920;
            Text text;
            if (!smithingScreen.getScreenHandler().getSlot(3).hasStack())
                text = null;
            else {
                text = Text.translatable("unbreakable.container.repair.materialCost", repairCost);
            }

            if (text != null) {
                int displayX = this.backgroundWidth - 8 - smithingScreen.getTextRenderer().getWidth(text) - 2;
                context.fill(displayX - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
                context.drawTextWithShadow(smithingScreen.getTextRenderer(), text, displayX, 69, textColor);
            }
        }
    }
}
