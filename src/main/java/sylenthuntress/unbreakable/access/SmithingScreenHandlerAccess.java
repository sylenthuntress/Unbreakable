package sylenthuntress.unbreakable.access;

import org.spongepowered.asm.mixin.Unique;

public interface SmithingScreenHandlerAccess {
    @Unique
    int unbreakable$getRepairCost();
}
