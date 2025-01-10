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
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Mutable
    @Final
    @Shadow
    private final Property levelCost;
    @Unique
    int scaledWithShatterLevel = -1;

    public AnvilScreenHandlerMixin(Property levelCost) {
        this.levelCost = levelCost;
    }

    // Disables repairCost scaling from anvil uses
    @ModifyReturnValue(method = "getNextCost", at = @At(value = "RETURN"))
    private static int noRepairCostScaling(int original) {
        if (!Unbreakable.CONFIG.anvilRepair.COST.REPAIR_SCALING()) original = 0;
        return original;
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object scaleWithEnchantments(Object original, @Local(ordinal = 0) ItemStack inputStack) {
        float repairCost = (Integer) original;
        if (Unbreakable.CONFIG.anvilRepair.COST.ENCHANTMENT_SCALING())
            for (RegistryEntry<Enchantment> enchantment : inputStack.getEnchantments().getEnchantments())
                repairCost += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), inputStack);
        return (int) repairCost;
    }

    @ModifyVariable(method = "updateResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), ordinal = 0)
    private int scaleWithShatterLevel(int original, @Local(ordinal = 0) ItemStack inputStack, @Local(ordinal = 2) ItemStack secondInputStack, @Local(ordinal = 1) ItemStack outputStack) {
        int oldShatterLevel = inputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
        int newShatterLevel = outputStack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
        if (scaledWithShatterLevel != newShatterLevel && Unbreakable.CONFIG.anvilRepair.COST.SHATTER_SCALING() && oldShatterLevel > newShatterLevel) {
            long initialRepairCost = (long) inputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + (long) secondInputStack.getOrDefault(DataComponentTypes.REPAIR_COST, 0) + 1;
            if (Unbreakable.CONFIG.anvilRepair.COST.ENCHANTMENT_SCALING())
                for (RegistryEntry<Enchantment> enchantment : inputStack.getEnchantments().getEnchantments())
                    initialRepairCost += ItemShatterHelper.getEnchantmentLevel(enchantment.getKey().orElseThrow(), inputStack);
            original += (int) initialRepairCost;
        }
        scaledWithShatterLevel = newShatterLevel;
        return original;
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    void clearDegradation(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (Unbreakable.CONFIG.smithingRepair.COST.ANVILS_CLEAR_DEGRADATION())
            stack.set(ModComponents.SMITHING_DEGRADATION, 0);
    }

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "CONSTANT", args = "intValue=4"))
    private int repairCostFactor(int original) {
        return Math.round(original * Unbreakable.CONFIG.anvilRepair.COST.MULTIPLIER());
    }

    // Allows the player to repair items beyond the normal 40-level limit
    @ModifyExpressionValue(method = "updateResult", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z", ordinal = 1))
    private boolean disableTooExpensiveClient(boolean original) {
        return !Unbreakable.CONFIG.anvilRepair.TOO_EXPENSIVE();
    }
}
