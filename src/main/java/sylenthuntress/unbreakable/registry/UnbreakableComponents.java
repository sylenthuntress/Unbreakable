package sylenthuntress.unbreakable.registry;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import sylenthuntress.unbreakable.Unbreakable;

import java.util.Map;

public class UnbreakableComponents {
    public static final ComponentType<Integer> SHATTER_LEVEL = register("shatter_level");
    public static final ComponentType<Integer> MAX_SHATTER_LEVEL = register("max_shatter_level");

    public static final ComponentType<Map<String, Integer>> DEGRADATION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Unbreakable.modIdentifier("degradation"),
            ComponentType.<Map<String, Integer>>builder().codec(Codec.unboundedMap(Codec.STRING, Codec.INT)).build()
    );

    private static ComponentType<Integer> register(String id) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Unbreakable.modIdentifier(id),
                ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 255)).build()
        );
    }

    public static void initialize() {

    }
}
