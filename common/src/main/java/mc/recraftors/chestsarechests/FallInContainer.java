package mc.recraftors.chestsarechests;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Map;

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

    default boolean chests$fallIn(World world, BlockPos pos, BlockState state, ItemEntity entity,
                               BlockEntity blockEntity) {
        if (!state.hasBlockEntity() || !chests$isOpen()) return false;
        if (VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(entity.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ())), chests$InputAreaShape(), BooleanBiFunction.AND)) {
            return chests$tryInsertion(entity, state, blockEntity);
        }
        return false;
    }

    default boolean chests$tryInsertion(ItemEntity entity, BlockState state, BlockEntity block) {
        return false;
    }

    default boolean chests$isOpen() {
        return false;
    }

    default VoxelShape chests$InputAreaShape() {
        return EMPTY;
    }
}
