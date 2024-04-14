package mc.recraftors.chestsarechests.util;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.unruled_api.utils.IGameRulesProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import oshi.util.tuples.Pair;

/**
 * Utility class for items with special fall-from-container behaviour.
 */
public interface ContainerItemHelper {
    default boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        return false;
    }

    default Direction[] chests$getFallDirection(ItemStack stack) {
        return new Direction[]{Direction.DOWN};
    }

    static boolean defaultOnOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, ((ContainerItemHelper)stack.getItem()).chests$getFallDirection(stack))) return false;
        float   offX = 0,
                offY = 0,
                offZ = 0;
        Pair<Float, Float> pair = getRandomSpreadOffset(((IGameRulesProvider)world.getGameRules()).unruled_getFloat(ChestsAreChests.getBarrelFallRandomSpreadRadius()));
        switch (direction) {
            case UP, DOWN -> {
                offX = pair.getA();
                offZ = pair.getB();
            }
            case EAST, WEST -> {
                offY = pair.getA();
                offZ = pair.getB();
            }
            case NORTH, SOUTH -> {
                offX = pair.getA();
                offY = pair.getB();
            }
        }
        ItemEntity entity = new ItemEntity(world, pos.x + offX, pos.y + offY, pos.z + offZ, stack.copy());
        entity.setVelocity(velocity);
        world.spawnEntity(entity);
        return true;
    }

    static Pair<Float, Float> getRandomSpreadOffset(float f) {
        if (f == 0) return new Pair<>(0f, 0f);
        f = Math.abs(f);
        float f1 = (float) Math.random();
        float f2 = (float) (Math.random() * Math.sin(Math.acos(f1)));
        float f3 = f/2;
        return new Pair<>(f1 - f3, f2 - f3);
    }
}
