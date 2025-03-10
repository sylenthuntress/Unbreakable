package sylenthuntress.unbreakable.mixin.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sylenthuntress.unbreakable.Unbreakable;
import sylenthuntress.unbreakable.config.util.ConfigHelper;
import sylenthuntress.unbreakable.registry.UnbreakableTags;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

import java.util.function.Consumer;

@SuppressWarnings("ConstantValue")
@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack implements ComponentHolder {
    @Unique
    boolean incrementedShatterLevel;

    @Shadow
    public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    // Prevents items from breaking at normal values
    @ModifyReturnValue(
            method = "shouldBreak",
            at = @At(value = "RETURN")
    )
    private boolean unbreakable$preventItemBreak(boolean original) {
        return unbreakable$preventItemBreakCondition(original);
    }

    @ModifyReturnValue(
            method = "willBreakNextUse",
            at = @At(value = "RETURN")
    )
    private boolean unbreakable$itemWontBreak(boolean original) {
        return unbreakable$preventItemBreakCondition(original);
    }

    @Unique
    private boolean unbreakable$preventItemBreakCondition(boolean original) {
        return (Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0
                || (Unbreakable.CONFIG.breakItems()
                && ItemShatterHelper.isMaxShatterLevel((ItemStack) (Object) this)
                && (this.getDamage() >= this.getMaxDamage() * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1)))
                || this.isIn(UnbreakableTags.BREAKABLE_ITEMS)) && original;
    }

    // Allows items to enter negative durability (twice their normal MAX_DAMAGE)
    @Unique
    private int unbreakable$disableDamageCap(int original) {
        if (Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0
                || ConfigHelper.isInList$shatterBlacklist(this.getRegistryEntry())) {
            return original;
        }
        return (int) (original * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1) + 1);
    }

    @ModifyExpressionValue(
            method = "setDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            )
    )
    private int unbreakable$disableSetDamageCap(int original) {
        return this.unbreakable$disableDamageCap(original);
    }

    @ModifyExpressionValue(
            method = "getDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            )
    )
    private int unbreakable$disableGetDamageCap(int original) {
        return this.unbreakable$disableDamageCap(original);
    }

    @ModifyExpressionValue(
            method = "damage(ILnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            )
    )
    private int unbreakable$disableApplyDamageCap(int original) {
        return this.unbreakable$disableDamageCap(original);
    }

    @Unique
    private Consumer<Item> breakCallback;

    @Inject(
            method = "onDurabilityChange",
            at = @At("HEAD")
    )
    private void unbreakable$applyBreakFx(int damage, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback,
                                          CallbackInfo ci) {
        this.breakCallback = breakCallback;
    }

    @Inject(
            method = "setDamage",
            at = @At("TAIL")
    )
    private void unbreakable$applyShatter(int damage, CallbackInfo ci) {
        ItemShatterHelper.applyShatter((ItemStack) (Object) this, damage, breakCallback);
        this.breakCallback = null;
    }
}