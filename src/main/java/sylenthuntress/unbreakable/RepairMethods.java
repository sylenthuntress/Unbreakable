package sylenthuntress.unbreakable;

import net.minecraft.util.Identifier;

public enum RepairMethods {
    SMITHING_TABLE("smithing_table"),
    GRINDSTONE("grindstone");

    private final Identifier name;

    RepairMethods(final String name) {
        this.name = Identifier.of(name);
    }

    public Identifier getName() {
        return this.name;
    }
}