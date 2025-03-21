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
import sylenthuntress.unbreakable.util.ShatterHelper;

import java.util.function.Consumer;

@SuppressWarnings("ConstantValue")
@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack implements ComponentHolder {
    @Shadow
    public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    @ModifyReturnValue(
            method = {
                    "shouldBreak",
                    "willBreakNextUse"
            },
            at = @At(value = "RETURN")
    )
    private boolean unbreakable$preventItemBreak(boolean original) {
        boolean itemIsBreakable = this.isIn(UnbreakableTags.BREAKABLE_ITEMS);
        boolean itemCanBreak = Unbreakable.CONFIG.breakItems() && ShatterHelper.isMaxShatterLevel((ItemStack) (Object) this);
        boolean noNegativeDurability = Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0;

        return original && (itemIsBreakable || itemCanBreak || noNegativeDurability);
    }

    @ModifyExpressionValue(
            method = {
                    "setDamage",
                    "getDamage",
                    "damage(ILnet/minecraft/entity/player/PlayerEntity;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            )
    )
    private int unbreakable$disableSetDamageCap(int original) {
        if (Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0F || ConfigHelper.isInList$shatterBlacklist(this.getRegistryEntry())) {
            return original;
        } else {
            return ShatterHelper.getMaxDamageWithNegatives((ItemStack) (Object) this) + 1;
        }
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
        ShatterHelper.applyShatter((ItemStack) (Object) this, damage, breakCallback);
        this.breakCallback = null;
    }
}