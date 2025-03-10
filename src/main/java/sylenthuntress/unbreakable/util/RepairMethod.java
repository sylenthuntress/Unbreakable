package sylenthuntress.unbreakable.util;

import net.minecraft.util.Identifier;

public enum RepairMethod {
    SMITHING_TABLE("smithing_table"),
    GRINDSTONE("grindstone");

    private final Identifier name;

    RepairMethod(final String name) {
        this.name = Identifier.of(name);
    }

    public Identifier getName() {
        return this.name;
    }

    public String getAsString() {
        return getName().toString().replaceFirst("^minecraft:", "");
    }
}