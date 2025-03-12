package sylenthuntress.unbreakable.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import sylenthuntress.unbreakable.registry.UnbreakableComponents;
import sylenthuntress.unbreakable.util.ShatterHelper;

import java.util.Collection;
import java.util.LinkedHashMap;

public abstract class ItemDamageCommand {
    static final SimpleCommandExceptionType NO_ITEM_FOUND_TARGET_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("commands.item.durability.entity.failure.no_item_found")
    );
    static final DynamicCommandExceptionType NO_SUCH_SLOT_TARGET_EXCEPTION = new DynamicCommandExceptionType(
            slot -> Text.stringifiedTranslatable("commands.item.durability.block.failure.no_such_slot", slot)
    );
    private static final Dynamic3CommandExceptionType NOT_A_CONTAINER_TARGET_EXCEPTION = new Dynamic3CommandExceptionType(
            (x, y, z) -> Text.stringifiedTranslatable("commands.item.durability.block.failure.not_a_container", x, y, z)
    );

    public static void register(CommandNode<ServerCommandSource> baseNode) {
        var commandNode = CommandManager.literal("damage")
                .requires(source -> source.hasPermissionLevel(2))
                .build();

        //  Add the sub-nodes as children of the main node
        commandNode.addChild(GetDamageNode.get());
        commandNode.addChild(AddDamageNode.get());
        commandNode.addChild(SetDamageNode.get());

        //  Add the main nodes as a child of the base node
        baseNode.getChild("item").addChild(commandNode);
    }

    public static Inventory getInventoryAtPos(ServerCommandSource source, BlockPos pos, Dynamic3CommandExceptionType exception) throws CommandSyntaxException {
        if (source.getWorld().getBlockEntity(pos) instanceof Inventory inventory) {
            return inventory;
        } else {
            throw exception.create(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static class GetDamageNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("get")
                    .then(CommandManager.literal("entity")
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("factor", FloatArgumentType.floatArg())
                                                    .executes(context -> executeEntity(context.getSource(), EntityArgumentType.getEntities(context, "targets"), ItemSlotArgumentType.getItemSlot(context, "slot"), FloatArgumentType.getFloat(context, "factor"))))
                                            .executes(context -> executeEntity(context.getSource(), EntityArgumentType.getEntities(context, "targets"), ItemSlotArgumentType.getItemSlot(context, "slot"), 1.0F))))
                    )
                    .then(CommandManager.literal("block")
                            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("factor", FloatArgumentType.floatArg())
                                                    .executes(context -> executeBlock(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemSlotArgumentType.getItemSlot(context, "slot"), FloatArgumentType.getFloat(context, "factor"))))
                                            .executes(context -> executeBlock(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemSlotArgumentType.getItemSlot(context, "slot"), 1.0F))))).build();
        }

        public static int executeEntity(ServerCommandSource source, Collection<? extends Entity> targets, int slot, float factor) throws CommandSyntaxException {
            LinkedHashMap<Entity, ItemStack> itemDurabilityMap = Maps.newLinkedHashMapWithExpectedSize(targets.size());

            StackReference stackReference;
            for (Entity entity : targets) {
                stackReference = entity.getStackReference(slot);
                if (stackReference == StackReference.EMPTY || stackReference.get().isEmpty()) {
                    continue;
                }

                ItemStack stack = stackReference.get();

                itemDurabilityMap.put(entity, stack);
            }

            if (itemDurabilityMap.isEmpty()) {
                throw NO_ITEM_FOUND_TARGET_EXCEPTION.create();
            } else if (itemDurabilityMap.size() == 1) {
                Entity entity = itemDurabilityMap.keySet().iterator().next();
                ItemStack stack = entity.getStackReference(slot).get();
                source.sendFeedback(
                        () -> Text.translatable(
                                "commands.item.durability.entity.success.get.single",
                                stack.toHoverableText(),
                                entity.getName(),
                                Math.round(stack.getDamage() * factor),
                                Text.translatable(
                                        "commands.item.durability.total",
                                        stack.getMaxDamage()
                                                - stack.getDamage(),
                                        stack.getMaxDamage()
                                ).formatted(Formatting.GRAY)
                        ), false
                );
            } else {
                MutableText successText = Text.translatable(
                        "commands.item.durability.entity.success.get.multiple",
                        itemDurabilityMap.size()
                );

                itemDurabilityMap.sequencedKeySet().forEach(entity
                                -> successText.append(
                                Text.translatable(
                                        "commands.item.durability.mapped_item.get",
                                        itemDurabilityMap.get(entity).toHoverableText(),
                                        entity.getName(),
                                        Math.round(itemDurabilityMap.get(entity).getDamage() * factor),
                                        Text.translatable(
                                                "commands.item.durability.total",
                                                itemDurabilityMap.get(entity).getMaxDamage()
                                                        - itemDurabilityMap.get(entity).getDamage(),
                                                itemDurabilityMap.get(entity).getMaxDamage()
                                        ).formatted(Formatting.GRAY)
                                ).append(
                                        entity.equals(itemDurabilityMap.sequencedKeySet().getLast())
                                                ? ";"
                                                : "\n"
                                )
                        )
                );

                source.sendFeedback(() -> successText, false);
            }

            return itemDurabilityMap.values().stream().mapToInt(stack -> Math.round(stack.getDamage() * factor)).sum();
        }

        public static int executeBlock(ServerCommandSource source, BlockPos pos, int slot, float factor) throws CommandSyntaxException {
            Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
            if (slot < 0 || slot >= inventory.size()) {
                throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
            }

            ItemStack stack = inventory.getStack(slot);

            int damage = stack.getDamage();

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.item.durability.block.success.get",
                            stack.toHoverableText(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            damage,
                            Text.translatable(
                                    "commands.item.durability.total",
                                    stack.getMaxDamage()
                                            - stack.getDamage(),
                                    stack.getMaxDamage()
                            ).formatted(Formatting.GRAY)
                    ), true
            );

            return Math.round(damage * factor);
        }
    }

    public static class AddDamageNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("add")
                    .then(CommandManager.literal("entity")
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                    .executes(context -> executeEntity(context.getSource(), EntityArgumentType.getEntities(context, "targets"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"), false))
                                                    .then(CommandManager.argument("canBreak", BoolArgumentType.bool())
                                                            .executes(context -> executeEntity(context.getSource(), EntityArgumentType.getEntities(context, "targets"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"), BoolArgumentType.getBool(context, "canBreak"))))))
                            ))
                    .then(CommandManager.literal("block")
                            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                                    .executes(context -> executeBlock(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"), false))
                                                    .then(CommandManager.argument("canBreak", BoolArgumentType.bool())
                                                            .executes(context -> executeBlock(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"), BoolArgumentType.getBool(context, "canBreak")))))))).build();
        }

        public static int executeEntity(ServerCommandSource source, Collection<? extends Entity> targets, int slot, int amount, boolean canBreak) throws CommandSyntaxException {
            LinkedHashMap<Pair<Entity, ItemStack>, Integer> itemDurabilityMap = Maps.newLinkedHashMapWithExpectedSize(targets.size());
            ItemStack displayStack = ItemStack.EMPTY;

            for (Entity entity : targets) {
                StackReference stackReference = entity.getStackReference(slot);
                if (stackReference == StackReference.EMPTY || stackReference.get().isEmpty()) {
                    continue;
                }

                ItemStack stack = stackReference.get();

                if (canBreak && amount > 0) {
                    displayStack = stack.copy();
                    ItemStack finalDisplayStack = displayStack;
                    if (entity.getWorld() instanceof ServerWorld serverWorld) {
                        stack.damage(amount, serverWorld, null, item -> {
                            if (entity instanceof LivingEntity livingEntity) {
                                livingEntity.sendEquipmentBreakStatus(stack.getItem(), EquipmentSlot.FROM_INDEX.apply(slot));
                            }

                            if (ShatterHelper.isShattered(stack)) {
                                source.sendFeedback(
                                        () -> Text.translatable(
                                                "commands.item.durability.entity.success.add.shattered",
                                                stack.toHoverableText(),
                                                entity.getName(),
                                                finalDisplayStack.get(UnbreakableComponents.SHATTER_LEVEL),
                                                ShatterHelper.getMaxShatterLevel(stack)
                                        ), true
                                );
                            } else {
                                source.sendFeedback(
                                        () -> Text.translatable(
                                                "commands.item.durability.entity.success.add.broke",
                                                finalDisplayStack.toHoverableText(),
                                                entity.getName()
                                        ), true
                                );
                            }
                        });
                    }
                } else {
                    stack.setDamage(stack.getDamage() + amount);
                }

                itemDurabilityMap.put(Pair.of(entity, stack), amount);
            }

            ItemStack finalDisplayStack = displayStack;
            if (itemDurabilityMap.isEmpty()) {
                throw NO_ITEM_FOUND_TARGET_EXCEPTION.create();
            } else if (itemDurabilityMap.size() == 1) {
                Entity entity = itemDurabilityMap.keySet().iterator().next().getFirst();
                ItemStack stack = entity.getStackReference(slot).get();
                source.sendFeedback(
                        () -> Text.translatable(
                                "commands.item.durability.entity.success.add.single",
                                amount,
                                stack.isEmpty() ? finalDisplayStack.toHoverableText() : stack.toHoverableText(),
                                entity.getName()
                        ), true
                );
            } else {
                MutableText successText = Text.translatable(
                        "commands.item.durability.entity.success.add.multiple",
                        itemDurabilityMap.size()
                );

                itemDurabilityMap.sequencedKeySet().forEach(pair
                                -> successText.append(
                                Text.translatable(
                                        "commands.item.durability.mapped_item.add",
                                        itemDurabilityMap.get(pair),
                                        pair.getSecond().toHoverableText(),
                                        pair.getFirst().getName()
                                ).append(
                                        pair.getSecond().equals(itemDurabilityMap.sequencedKeySet().getLast().getSecond())
                                                ? ";"
                                                : "\n"
                                )
                        )
                );

                source.sendFeedback(() -> successText, true);
            }

            return itemDurabilityMap.values().stream().mapToInt(Integer::intValue).sum();
        }

        public static int executeBlock(ServerCommandSource source, BlockPos pos, int slot, int amount, boolean canBreak) throws CommandSyntaxException {
            Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
            if (slot < 0 || slot >= inventory.size()) {
                throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
            }

            ItemStack stack = inventory.getStack(slot);
            ItemStack displayStack = stack.copy();

            if (canBreak && amount > 0) {
                stack.damage(amount, source.getWorld(), null, item -> {
                    if (ShatterHelper.isShattered(stack)) {
                        source.sendFeedback(
                                () -> Text.translatable(
                                        "commands.item.durability.block.success.damage.shattered",
                                        stack.toHoverableText(),
                                        pos.getX(),
                                        pos.getY(),
                                        pos.getZ(),
                                        stack.get(UnbreakableComponents.SHATTER_LEVEL),
                                        ShatterHelper.getMaxShatterLevel(stack)
                                ), true
                        );
                    } else {
                        source.sendFeedback(
                                () -> Text.translatable(
                                        "commands.item.durability.block.success.damage.broke",
                                        amount,
                                        stack.toHoverableText(),
                                        pos.getX(),
                                        pos.getY(),
                                        pos.getZ()
                                ), true
                        );
                    }
                });
            } else {
                stack.setDamage(stack.getDamage() + amount);
            }

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.item.durability.block.success.add",
                            amount,
                            stack.isEmpty() ? displayStack.toHoverableText() : stack.toHoverableText(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    ), true
            );

            return amount;
        }
    }

    public static class SetDamageNode {
        public static LiteralCommandNode<ServerCommandSource> get() {
            return CommandManager.literal("set")
                    .then(CommandManager.literal("entity")
                            .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                    .executes(context -> executeEntity(context.getSource(), EntityArgumentType.getEntities(context, "targets"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"))))))
                    )
                    .then(CommandManager.literal("block")
                            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                    .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(0))
                                                    .executes(context -> executeBlock(context.getSource(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), ItemSlotArgumentType.getItemSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"))))))).build();
        }

        public static int executeEntity(ServerCommandSource source, Collection<? extends Entity> targets, int slot, int amount) throws CommandSyntaxException {
            LinkedHashMap<Pair<Entity, ItemStack>, Pair<Integer, Integer>> itemDurabilityMap = Maps.newLinkedHashMapWithExpectedSize(targets.size());

            for (Entity entity : targets) {
                StackReference stackReference = entity.getStackReference(slot);
                if (stackReference == StackReference.EMPTY || stackReference.get().isEmpty()) {
                    continue;
                }

                ItemStack stack = stackReference.get();

                itemDurabilityMap.put(
                        Pair.of(entity, stack),
                        Pair.of(stack.getDamage(), Math.clamp(amount, 0, ShatterHelper.getMaxDamageWithNegatives(stack)))
                );

                stack.setDamage(amount);
            }

            if (itemDurabilityMap.isEmpty()) {
                throw NO_ITEM_FOUND_TARGET_EXCEPTION.create();
            } else if (itemDurabilityMap.size() == 1) {
                Entity entity = itemDurabilityMap.keySet().iterator().next().getFirst();
                ItemStack stack = entity.getStackReference(slot).get();
                source.sendFeedback(
                        () -> Text.translatable(
                                "commands.item.durability.entity.success.set.single",
                                stack.toHoverableText(),
                                entity.getName(),
                                stack.getDamage(),
                                amount
                        ), true
                );
            } else {
                MutableText successText = Text.translatable(
                        "commands.item.durability.entity.success.set.multiple",
                        itemDurabilityMap.size()
                );

                itemDurabilityMap.sequencedKeySet().forEach(pair
                                -> successText.append(
                                Text.translatable(
                                        "commands.item.durability.mapped_item.set",
                                        pair.getSecond().toHoverableText(),
                                        itemDurabilityMap.get(pair).getFirst(),
                                        itemDurabilityMap.get(pair).getSecond(),
                                        pair.getFirst().getName()
                                ).append(
                                        pair.getSecond().equals(itemDurabilityMap.sequencedKeySet().getLast().getSecond())
                                                ? ";"
                                                : "\n"
                                )
                        )
                );

                source.sendFeedback(() -> successText, true);
            }

            return amount;
        }

        public static int executeBlock(ServerCommandSource source, BlockPos pos, int slot, int amount) throws CommandSyntaxException {
            Inventory inventory = getInventoryAtPos(source, pos, NOT_A_CONTAINER_TARGET_EXCEPTION);
            if (slot < 0 || slot >= inventory.size()) {
                throw NO_SUCH_SLOT_TARGET_EXCEPTION.create(slot);
            }

            ItemStack stack = inventory.getStack(slot);

            source.sendFeedback(
                    () -> Text.translatable(
                            "commands.item.durability.block.success.set",
                            stack.toHoverableText(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            stack.getDamage(),
                            Math.clamp(amount, 0, ShatterHelper.getMaxDamageWithNegatives(stack))
                    ), true
            );

            stack.setDamage(amount);

            return amount;
        }
    }
}
