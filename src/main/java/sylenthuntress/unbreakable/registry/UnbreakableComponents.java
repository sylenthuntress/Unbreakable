package sylenthuntress.unbreakable.registry;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;
import sylenthuntress.unbreakable.Unbreakable;

import java.util.Map;

public abstract class UnbreakableComponents {
    public static final ComponentType<Integer> SHATTER_LEVEL = register("shatter_level", Codecs.rangedInt(0, 255));
    public static final ComponentType<Integer> MAX_SHATTER_LEVEL = register("max_shatter_level", Codecs.rangedInt(0, 255));
    public static final ComponentType<Map<String, Integer>> DEGRADATION = register("degradation", Codec.unboundedMap(Codec.STRING, Codec.INT));

    private static <T> ComponentType<T> register(String id, Codec<T> codec) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Unbreakable.modIdentifier(id),
                ComponentType.<T>builder().codec(codec).build()
        );
    }

    public static void initialize() {

    }
}
