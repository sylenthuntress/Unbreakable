package sylenthuntress.unbreakable.mixin.item_damage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import sylenthuntress.unbreakable.Unbreakable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @WrapOperation(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;discard()V"
            )
    )
    private void unbreakable$preventRemoval(ItemEntity instance, Operation<Void> original) {
        final ItemStack stack = this.getStack();

        if (!Unbreakable.CONFIG.damageItemEntities()) {
            original.call(instance);
            return;
        }

        World world = this.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            stack.damage(
                    1,
                    serverWorld,
                    null,
                    item -> playSound(
                            SoundEvents.ENTITY_ITEM_BREAK,
                            1,
                            1
                    )
            );
        }
    }
}
