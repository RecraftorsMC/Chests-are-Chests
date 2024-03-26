package mc.recraftors.chestsarechests.util;

import mc.recraftors.chestsarechests.ChestsAreChests;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Base container interface to be used for any container that should either
 * allow items falling in, falling out, or getting flung away.
 */
public interface FallInContainer {
    VoxelShape EMPTY = Block.createCuboidShape(0, 0, 0, 0, 0, 0);
    VoxelShape INSIDE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    VoxelShape BESIDE_POSITIVE_X = Block.createCuboidShape(14, 0, 0, 24, 16, 16);
    VoxelShape ABOVE = Block.createCuboidShape(0, 13, 0, 16, 23, 16);
    VoxelShape BESIDE_POSITIVE_Z = Block.createCuboidShape(0, 0, 14, 16, 16, 24);
    VoxelShape BESIDE_NEGATIVE_X = Block.createCuboidShape(-8, 0, 0, 2, 16, 16);
    VoxelShape BELOW = Block.createCuboidShape(0, -8, 0, 16, 2, 16);
    VoxelShape BESIDE_NEGATIVE_Z = Block.createCuboidShape(0, 0, -8, 16, 16, 2);
    VoxelShape AROUND_AGAINST = Block.createCuboidShape(-2, -2, -2, 18, 18, 18);
    VoxelShape AROUND = VoxelShapes.union(INSIDE, BESIDE_POSITIVE_X, ABOVE, BESIDE_POSITIVE_Z, BESIDE_NEGATIVE_X, BELOW, BESIDE_NEGATIVE_Z);
    VoxelShape SIDES = VoxelShapes.union(BESIDE_POSITIVE_X, BESIDE_NEGATIVE_X, BESIDE_POSITIVE_Z, BESIDE_NEGATIVE_Z);

    Map<Direction, VoxelShape> DIRECTION_SHAPES = Map.of(
            Direction.EAST, VoxelShapes.union(INSIDE, BESIDE_POSITIVE_X),
            Direction.UP, VoxelShapes.union(INSIDE, ABOVE),
            Direction.SOUTH, VoxelShapes.union(INSIDE, BESIDE_POSITIVE_Z),
            Direction.WEST, VoxelShapes.union(INSIDE, BESIDE_NEGATIVE_X),
            Direction.DOWN, VoxelShapes.union(INSIDE, BELOW),
            Direction.NORTH, VoxelShapes.union(INSIDE, BESIDE_NEGATIVE_Z)
    );

    /**
     * Handles item insertion, using other methods.
     * @param pos The current container's position.
     * @param state The current container's block state.
     * @param entity The item to try to insert.
     * @return Whether the item could be inserted.
     */
    @ApiStatus.NonExtendable
    default boolean chests$fallIn(BlockPos pos, BlockState state, ItemEntity entity) {
        if (!state.hasBlockEntity() || !chests$isOpen()) return false;
        if (VoxelShapes.matchesAnywhere(
                VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())),
                chests$InputAreaShape(), BooleanBiFunction.AND)) {
            return chests$tryInsertion(entity);
        }
        return false;
    }

    /**
     * Attempts inserting the provided item entity in the current container.
     * <p>
     * Process can be eased by making use of {@link #chests$inventoryInsertion(DefaultedList, ItemEntity, BiConsumer)}.
     * <p>
     * Returns whether the item entity was completely inserted (<i>consumed</i>).
     * @param entity The item entity to attempt insertion of in the current container.
     * @return Whether the item entity was completely inserted.
     * @see mc.recraftors.chestsarechests.mixin.block_entities.ChestBlockEntityMixin#chests$tryInsertion(ItemEntity)
     */
    default boolean chests$tryInsertion(ItemEntity entity) {
        return false;
    }

    /**
     * Returns whether the current container is open or not.
     * @return Whether the current container is open or not.
     */
    default boolean chests$isOpen() {
        return false;
    }

    /**
     * Returns the current container's input shape.
     * <p>
     * Should be centered around a 0-0-0 block, offset is managed outside of method.
     * @return The current container's input shape.
     * @see mc.recraftors.chestsarechests.mixin.block_entities.BarrelBlockEntityMixin#chests$InputAreaShape()
     */
    default VoxelShape chests$InputAreaShape() {
        return EMPTY;
    }

    /**
     * Force-opens the current container. Executed when an empty dispenser fires at the current container.
     * <p>
     * Should be executed (directly or not) by {@link #chests$tryForceOpen(BlockState)}.
     * @param world The world (dimension) in which the container is.
     * @param at The block position of the container.
     * @param from The block state issuing the force-opening attempt. (Usually, a dispenser block state)
     */
    default void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {}

    /**
     * Attempts force-opening of the current container. Executed when an empty dispenser fires at the current container
     * if it is closed.
     * <p>
     * Allows to add a check layer between force-opening attempt and actual realisation.
     * {@link #chests$forceOpen(ServerWorld, BlockPos, BlockState)} is expected to be executed from this method, but
     * full opening implementation can be done in this method, leaving
     * {@link #chests$forceOpen(ServerWorld, BlockPos, BlockState)} at its default (empty) implementation.
     * @param from The block state issuing the force-opening attempt. (Usually, a dispenser block state)
     * @return Whether the block could be force-opened. By default, {@code false}.
     */
    default boolean chests$tryForceOpen(BlockState from) {
        return false;
    }

    /**
     * Attempts force-closing the current container. Executed when an empty dispenser fires at the current container if
     * it is open.
     * @return Whether the block could be force-closed. By default, {@code false}.
     */
    default boolean chests$forceClose() {
        return false;
    }

    /**
     * Returns the current container's {@link BlockOpenableContainer}. Can either be the container itself, or some
     * inner attribute. Usually also is the block state manager. (ViewerCountManager)
     * <p>
     * Only used internally of the {@link FallInContainer} implementation. Can be left unimplemented if not used.
     * <p>
     * Used by the vanilla {@link mc.recraftors.chestsarechests.mixin.block_entities.ChestBlockEntityMixin chest} /
     * {@link mc.recraftors.chestsarechests.mixin.block_entities.BarrelBlockEntityMixin barrel} implementations,
     * hence easing custom implementation if extending the vanilla chest or barrel classes.
     * @return The current container's {@link BlockOpenableContainer}.
     */
    default @Nullable BlockOpenableContainer chests$getContainer() {
        return null;
    }

    /**
     * Returns the current container's {@link BooleanHolder}. Can either be the container itself, or some inner
     * attribute. Usually also holds the open property. (LidFlingAnimator)
     * <p>
     * Only used internally of the {@link FallInContainer container} implementation.
     * Can be left unimplemented if not used.
     * <p>
     * Used by the vanilla {@link mc.recraftors.chestsarechests.mixin.block_entities.ChestBlockEntityMixin chest}
     * implementation, hence easing custom implementation if extending the vanilla chest or barrel classes.
     * @return The current container's {@link BooleanHolder}.
     */
    default @Nullable BooleanHolder chests$getBooleanHolder() {
        return null;
    }

    /**
     * Returns the current container's item fall update map. It is only used for the
     * {@link ChestsAreChests#BARREL_FALL_RULE_ID} function, in order to optimise running fall methods
     * multiple times on stacks that wouldn't be affected in the current context.
     * <p>
     * No inner use is needed, and any should be avoided. All use is managed in
     * {@link #chests$fallOut(World, Direction, Inventory, Vec3d, Vec3d)}.
     * <p>
     * Needs to be a custom, consistent but non-persistent integer-to-integer map. Return {@code null} to
     * prevent any item fall.
     * @return The current container's item fall update map.
     */
    default Map<Integer, Integer> chests$getFallUpdateMap() {
        return new HashMap<>();
    }

    /**
     * Makes items fall out... Or fly out, depending on the items
     * and current gamerules values.
     * <p>
     * Should be called in block entity tick.
     * @param world The container's world.
     * @param direction The opening direction.
     * @param inventory The container's inventory.
     * @param pos The container's opening position.
     * @param velocity The base item fall velocity.
     */
    @ApiStatus.NonExtendable
    default void chests$fallOut(World world, Direction direction, Inventory inventory, Vec3d pos, Vec3d velocity) {
        int m = inventory.size();
        boolean doSpecial = world.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial());
        Map<Integer, Integer> map = chests$getFallUpdateMap();
        if (map == null) {
            return;
        }
        for (int i = 0; i < m; i++) {
            ItemStack stack = inventory.getStack(i);
            int h = ChestsAreChests.itemStackCustomHash(stack);
            if (map.getOrDefault(i, 0).equals(h)) continue;
            boolean b = (doSpecial && stack.isIn(ChestsAreChests.SPECIAL_FALL)) ?
                    ((ContainerItemHelper)stack.getItem()).chests$onOpenTick(stack, this, direction, (ServerWorld) world, pos, velocity) :
                    ContainerItemHelper.defaultOnOpenTick(stack, this, direction, world, pos, velocity);
            if (b) {
                inventory.removeStack(i);
                map.remove(i);
            } else map.put(i, h);
        }
    }

    static boolean chests$inventoryInsertion(DefaultedList<ItemStack> inv, ItemEntity item, BiConsumer<Integer, ItemStack> setStack) {
        ItemStack stack = item.getStack().copy();
        int size = inv.size();
        boolean success = false;
        for (int t = 0; t < size && !stack.isEmpty(); t++) {
            ItemStack target = inv.get(t);
            if (target.isEmpty()) {
                setStack.accept(t, stack);
                stack = ItemStack.EMPTY;
                success = true;
            } else if (ChestsAreChests.canMergeItems(stack, target)) {
                int capability = stack.getMaxCount() - target.getCount();
                int amount = Math.min(stack.getCount(), capability);
                if (amount > 0) {
                    stack.decrement(amount);
                    target.increment(amount);
                    success = true;
                }
            }
        }
        if (success) {
            if (stack.isEmpty()) {
                item.discard();
            } else {
                item.setStack(stack);
            }
        }
        return success;
    }
}
