package sylenthuntress.unbreakable.registry;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import sylenthuntress.unbreakable.Unbreakable;

public class UnbreakableComponents {
    public static final ComponentType<Integer> SHATTER_LEVEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Unbreakable.modIdentifier("shatter_level"),
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 255)).build()
    );
    public static final ComponentType<Integer> MAX_SHATTER_LEVEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Unbreakable.modIdentifier("max_shatter_level"),
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 255)).build()
    );
    public static final ComponentType<Integer> SMITHING_DEGRADATION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Unbreakable.modIdentifier("smithing_degradation"),
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 20)).build()
    );
    public static final ComponentType<Integer> GRINDING_DEGRADATION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Unbreakable.modIdentifier("grinding_degradation"),
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 40)).build()
    );

    public static void initialize() {

    }
}
