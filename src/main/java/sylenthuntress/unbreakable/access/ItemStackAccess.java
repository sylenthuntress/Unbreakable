package sylenthuntress.unbreakable.access;

import org.spongepowered.asm.mixin.Unique;

public interface ItemStackAccess {
    @Unique
    boolean unbreakable$incrementedShatterLevel();
}
