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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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

    default boolean chests$fallIn(BlockPos pos, BlockState state, ItemEntity entity) {
        if (!state.hasBlockEntity() || !chests$isOpen()) return false;
        if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), chests$InputAreaShape(), BooleanBiFunction.AND)) {
            return chests$tryInsertion(entity);
        }
        return false;
    }

    default boolean chests$tryInsertion(ItemEntity entity) {
        return false;
    }

    default boolean chests$isOpen() {
        return false;
    }

    default VoxelShape chests$InputAreaShape() {
        return EMPTY;
    }

    default void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {}

    default boolean chests$tryForceOpen(BlockState from) {
        return false;
    }

    default boolean chests$forceClose() {
        return false;
    }

    default @Nullable BlockOpenableContainer chests$getContainer() {
        return null;
    }

    default @Nullable BooleanHolder chests$getBooleanHolder() {
        return null;
    }

    default Map<Integer, Integer> getFallUpdateMap() {
        return new HashMap<>();
    }

    default void chests$fallOut(World world, Direction direction, Inventory inventory, Vec3d pos, Vec3d velocity) {
        int m = inventory.size();
        Map<Integer, Integer> map = getFallUpdateMap();
        for (int i = 0; i < m; i++) {
            ItemStack stack = inventory.getStack(i);
            int h = ChestsAreChests.itemStackCustomHash(stack);
            if (map.getOrDefault(i, 0).equals(h)) continue;
            boolean b = stack.isIn(ChestsAreChests.SPECIAL_FALL) ?
                    ((ContainerItemHelper)stack.getItem()).chests$onOpenTick(stack, this, direction, world, pos, velocity) :
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
