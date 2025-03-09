package sylenthuntress.unbreakable.registry;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import sylenthuntress.unbreakable.Unbreakable;

public class UnbreakableComponents {
    public static final ComponentType<Integer> SHATTER_LEVEL = register(
            "shatter_level",
            255
    );
    public static final ComponentType<Integer> MAX_SHATTER_LEVEL = register(
            "max_shatter_level",
            255
    );
    public static final ComponentType<Integer> SMITHING_DEGRADATION = register(
            "smithing_degradation",
            20
    );
    public static final ComponentType<Integer> GRINDING_DEGRADATION = register(
            "grinding_degradation",
            40
    );

    private static ComponentType<Integer> register(String id, int max) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Unbreakable.modIdentifier(id),
                ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, max)).build()
        );
    }

    public static void initialize() {

    }
}
