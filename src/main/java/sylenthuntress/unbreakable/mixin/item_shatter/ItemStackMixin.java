package sylenthuntress.unbreakable.mixin.item_shatter;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import sylenthuntress.unbreakable.access.ItemStackAccess;
import sylenthuntress.unbreakable.util.ItemShatterHelper;
import sylenthuntress.unbreakable.util.ModComponents;
import sylenthuntress.unbreakable.util.Unbreakable;

import java.util.function.Consumer;

import static sylenthuntress.unbreakable.util.DataTagKeys.BREAKABLE_ITEMS;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccess {
    @Unique
    boolean incrementedShatterLevel;

    @Override
    public boolean unbreakable$incrementedShatterLevel() {
        return incrementedShatterLevel;
    }

    @Shadow
    public abstract <T> T set(ComponentType<? super T> type, @Nullable T value);

    @Shadow
    public abstract boolean isIn(TagKey<Item> tag);

    @Shadow
    public abstract ItemStack copy();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract RegistryEntry<Item> getRegistryEntry();

    // Prevents items from breaking at normal values
    @ModifyReturnValue(method = "shouldBreak", at = @At(value = "RETURN"))
    private boolean shouldBreak$preventItemBreak(boolean original) {
        return (Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0 || (Unbreakable.CONFIG.breakItems() && ItemShatterHelper.isMaxShatterLevel((ItemStack) (Object) this) && (this.getDamage() >= this.getMaxDamage() * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1))) || this.isIn(BREAKABLE_ITEMS)) && original;
    }

    @ModifyReturnValue(method = "willBreakNextUse", at = @At(value = "RETURN"))
    private boolean willBreakNextUse$preventItemBreak(boolean original) {
        return (Unbreakable.CONFIG.negativeDurabilityMultiplier() == 0.0 || (Unbreakable.CONFIG.breakItems() && ItemShatterHelper.isMaxShatterLevel((ItemStack) (Object) this) && (this.getDamage() >= this.getMaxDamage() * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1))) || this.isIn(BREAKABLE_ITEMS)) && original;
    }

    // Allows items to enter negative durability (twice their normal MAX_DAMAGE)
    @Unique
    private int disableDamageCap(int original) {
        if (Unbreakable.CONFIG.negativeDurabilityMultiplier() != 0.0 && !ItemShatterHelper.isInList$shatterBlacklist(this.getRegistryEntry()))
            return (int) (original * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1) + 1);
        return original;
    }

    @ModifyExpressionValue(method = "setDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"))
    private int setDamage$disableDamageCap(int original) {
        return this.disableDamageCap(original);
    }

    @ModifyExpressionValue(method = "getDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"))
    private int getDamage$disableDamageCap(int original) {
        return this.disableDamageCap(original);
    }

    @ModifyExpressionValue(method = "damage(ILnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"))
    private int damage$disableDamageCap(int original) {
        return this.disableDamageCap(original);
    }

    // Adds one stack of shattered when an item stack hits -1 durability.
    // Adds another stack and sets durability to -1 when the item hits rock bottom.
    @Inject(method = "setDamage", at = @At(value = "TAIL"))
    private void applyShatter(int damage, CallbackInfo ci) {
        incrementedShatterLevel = false;
        ItemStack stack = this.copy();
        if (ItemShatterHelper.getMaxShatterLevel(stack) > 0 && !ItemShatterHelper.isInList$shatterBlacklist(this.getRegistryEntry())) {
            int shatterLevel = stack.getOrDefault(ModComponents.SHATTER_LEVEL, 0);
            int maxShatterLevel = ItemShatterHelper.getMaxShatterLevel(stack);
            int maxDamage = this.getMaxDamage();
            if (damage <= maxDamage && Unbreakable.CONFIG.allowRepairingShattered() && ItemShatterHelper.isShattered(stack)) {
                shatterLevel--;
                if (shatterLevel > 0) {
                    damage = (int) (maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1));
                }
            } else if (damage > maxDamage && shatterLevel == 0) {
                shatterLevel++;
                incrementedShatterLevel = true;
            } else if (damage > maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1) && !ItemShatterHelper.isMaxShatterLevel(stack)) {
                shatterLevel++;
                damage = maxDamage + 1;
                incrementedShatterLevel = true;
            }
            this.set(ModComponents.SHATTER_LEVEL, Math.min(shatterLevel, maxShatterLevel));
            this.set(DataComponentTypes.DAMAGE, Math.clamp(damage, 0, (int) (maxDamage * (Unbreakable.CONFIG.negativeDurabilityMultiplier() + 1))));
        }
    }

    @Inject(method = "onDurabilityChange", at = @At(value = "TAIL"))
    private void breakCallback(int damage, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        if (incrementedShatterLevel) breakCallback.accept(this.getItem());
    }
}