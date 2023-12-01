package mc.recraftors.chestsarechests.util;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public interface BlockOpenableContainer {
    default boolean chests$openContainerBlock(ServerWorld world, BlockPos pos, BlockState state, FallInContainer container) {
        return false;
    }

    default boolean chests$shouldStayOpen(ServerWorld world) {
        return false;
    }

    default void chests$forceClose(World world, BlockPos pos) {}

    default boolean chests$isForcedOpened(ServerWorld world) {
        return false;
    }

    default Collection<ServerPlayerEntity> chests$getViewers() {
        return List.of();
    }
}
