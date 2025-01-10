package sylenthuntress.unbreakable.access;

import org.spongepowered.asm.mixin.Unique;

public interface GrindstoneScreenHandlerAccess {
    @Unique
    int unbreakable$getRepairCost();
}
