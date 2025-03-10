package sylenthuntress.unbreakable.mixin.item_repair.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.RepairHelper;

import java.util.HashMap;
import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public class Mixin_AnvilScreenHandler {
    @Mutable
    @Final
    @Shadow
    private final Property levelCost;
    @Unique
    int unbreakable$scaledWithShatterLevel = -1;

    public Mixin_AnvilScreenHandler(Property levelCost) {
        this.levelCost = levelCost;
    }

    // Disables repairCost scaling from anvil uses
    @ModifyReturnValue(
            method = "getNextCost",
            at = @At(value = "RETURN")
    )
    private static int unbreakable$noRepairCostScaling(int original) {
        if (Unbreakable.CONFIG.anvilRepair.COST.REPAIR_SCALING()) {
            return original;
        }

        return 0;
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;canRepairWith(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean unbreakable$cancelAnvilRepair(boolean original) {
        return original && Unbreakable.CONFIG.anvilRepair.ALLOW();
    }

    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object unbreakable$scaleWithEnchantments(Object original, @Local(ordinal = 0) ItemStack inputStack) {
        float repairCost = (Integer) original;

        if (Unbreakable.CONFIG.anvilRepair.COST.ENCHANTMENT_SCALING()) {
            for (RegistryEntry<Enchantment> enchantment : inputStack.getEnchantments().getEnchantments()) {
                repairCost += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), inputStack);
            }
        }

        return (int) repairCost;
    }

    @ModifyVariable(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Math;min(II)I",
                    ordinal = 1
            ),
            ordinal = 0
    )
    private int unbreakable$scaleWithShatterLevel(
            int original,
            @Local(ordinal = 0) ItemStack inputStack,
            @Local(ordinal = 2) ItemStack secondInputStack,
            @Local(ordinal = 1) ItemStack outputStack) {
        final int oldShatterLevel = inputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        int newShatterLevel = outputStack.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        long initialRepairCost = 0;

        if (unbreakable$scaledWithShatterLevel != newShatterLevel
                && Unbreakable.CONFIG.anvilRepair.COST.SHATTER_SCALING()
                && oldShatterLevel > newShatterLevel) {
            initialRepairCost = (long) inputStack.getOrDefault(
                    DataComponentTypes.REPAIR_COST,
                    0
            ) + (long) secondInputStack.getOrDefault(
                    DataComponentTypes.REPAIR_COST,
                    0
            ) + 1;
        }

        if (Unbreakable.CONFIG.anvilRepair.COST.ENCHANTMENT_SCALING()) {
            for (RegistryEntry<Enchantment> enchantment : inputStack.getEnchantments().getEnchantments()) {
                initialRepairCost += ItemShatterHelper.getEnchantmentLevel(
                        enchantment.getKey().orElseThrow(),
                        inputStack
                );
            }
        }

        original += (int) initialRepairCost;
        unbreakable$scaledWithShatterLevel = newShatterLevel;
        return original;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    void unbreakable$clearDegradation(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        var degradationMap = new HashMap<>(stack.getOrDefault(UnbreakableComponents.DEGRADATION, Map.of()));
        if (Unbreakable.CONFIG.smithingRepair.COST.ANVILS_CLEAR_DEGRADATION()) {
            degradationMap.remove(RepairHelper.RepairStation.SMITHING_TABLE.getName().toString());
        }
        if (Unbreakable.CONFIG.grindingRepair.COST.ANVILS_CLEAR_DEGRADATION()) {
            degradationMap.remove(RepairHelper.RepairStation.GRINDSTONE.getName().toString());
        }
        stack.set(UnbreakableComponents.DEGRADATION, degradationMap);
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "CONSTANT", args = "intValue=4"))
    private int unbreakable$repairCostFactor(int original) {
        return Math.round(original * Unbreakable.CONFIG.anvilRepair.COST.MULTIPLIER());
    }

    // Allows the player to repair items beyond the normal 40-level limit
    @ModifyExpressionValue(method = "updateResult", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z", ordinal = 1))
    private boolean unbreakable$disableTooExpensiveClient(boolean original) {
        return !Unbreakable.CONFIG.anvilRepair.TOO_EXPENSIVE();
    }
}
