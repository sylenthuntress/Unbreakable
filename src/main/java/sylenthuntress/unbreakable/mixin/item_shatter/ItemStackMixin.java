package sylenthuntress.unbreakable.mixin.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
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
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.registry.UnbreakableTags;
import sylenthuntress.unbreakable.util.ItemShatterHelper;

import java.util.function.Consumer;

@SuppressWarnings("ConstantValue")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {
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
                || ItemShatterHelper.isInList$shatterBlacklist(this.getRegistryEntry())) {
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

    // Adds one stack of shattered when an item stack hits -1 durability.
    // Adds another stack and sets durability to -1 when the item hits rock bottom.
    @Inject(
            method = "setDamage",
            at = @At("TAIL")
    )
    private void unbreakable$applyShatter(int damage, CallbackInfo ci) {
        final ItemStack stack = (ItemStack) (Object) this;

        if (ItemShatterHelper.getMaxShatterLevel(stack) == 0
                || ItemShatterHelper.isInList$shatterBlacklist(this.getRegistryEntry())) {
            return;
        }

        int shatterLevel = this.getOrDefault(UnbreakableComponents.SHATTER_LEVEL, 0);
        final int maxShatterLevel = ItemShatterHelper.getMaxShatterLevel(stack);
        final int maxDamage = this.getMaxDamage();

        if (damage <= maxDamage && Unbreakable.CONFIG.allowRepairingShattered()
                && ItemShatterHelper.isShattered(stack)) {
            shatterLevel--;

            if (shatterLevel > 0) {
                damage = Math.round(
                        maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1)
                );
            }
        } else if (damage > maxDamage && shatterLevel == 0) {
            shatterLevel++;
            incrementedShatterLevel = true;
        } else if (damage > maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1)
                && !ItemShatterHelper.isMaxShatterLevel(stack)) {
            shatterLevel++;
            damage = maxDamage + 1;
            incrementedShatterLevel = true;
        }

        this.set(
                UnbreakableComponents.SHATTER_LEVEL,
                Math.min(
                        shatterLevel,
                        maxShatterLevel
                )
        );

        this.set(
                DataComponentTypes.DAMAGE,
                Math.clamp(
                        damage,
                        0,
                        Math.round(
                                maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1)
                        )
                )
        );
    }

    @Inject(
            method = "onDurabilityChange",
            at = @At("TAIL")
    )
    private void unbreakable$applyBreakFx(
            int damage,
            @Nullable ServerPlayerEntity player,
            Consumer<Item> breakCallback,
            CallbackInfo ci) {
        if (incrementedShatterLevel) {
            breakCallback.accept(this.getItem());
            incrementedShatterLevel = false;
        }
    }
}