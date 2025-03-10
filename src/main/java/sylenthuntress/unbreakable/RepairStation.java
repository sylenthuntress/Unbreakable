package sylenthuntress.unbreakable;

import net.minecraft.util.Identifier;

public enum RepairStation {
    SMITHING_TABLE("smithing_table"),
    GRINDSTONE("grindstone");

    private final Identifier name;

    RepairStation(final String name) {
        this.name = Identifier.of(name);
    }

    public Identifier getName() {
        return this.name;
    }
}