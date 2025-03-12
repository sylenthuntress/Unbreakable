package sylenthuntress.unbreakable.mixin.item_repair.mending;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.RepairMethod;

import java.util.HashMap;
import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public class Mixin_ExperienceOrbEntity {
    @ModifyExpressionValue(
            method = "repairPlayerGears",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;min(II)I"
            )
    )
    private int degradeMendingRepair(int original, @Local ItemStack stack) {
        if (Unbreakable.CONFIG.mendingTweaks.DEGRADE()) {
            return original;
        }

        var degradationMap = new HashMap<>(stack.getOrDefault(UnbreakableComponents.DEGRADATION, Map.of()));
        int degradation = degradationMap.getOrDefault(RepairMethod.MENDING.getAsString(), 0);
        degradation = Math.round(degradation * Unbreakable.CONFIG.mendingTweaks.DEGRADE_MULTIPLIER());

        original = Math.round(original * (1 - degradation * 0.01F));

        if (original == 0) {
            return 0;
        }

        int newDegradation = Math.min(100, ++degradation);
        degradationMap.put(RepairMethod.MENDING.getAsString(), newDegradation);
        stack.set(UnbreakableComponents.DEGRADATION, degradationMap);

        return original;
    }
}
