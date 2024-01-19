package mc.recraftors.chestsarechests.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ContainerItemHelper {
    default boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        return false;
    }

    default Direction chests$getFallDirection(ItemStack stack) {
        return Direction.DOWN;
    }

    static boolean defaultOnOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        if (direction != ((ContainerItemHelper)stack.getItem()).chests$getFallDirection(stack)) return false;
        ItemEntity entity = new ItemEntity(world, pos.x, pos.y, pos.z, stack.copy());
        entity.setVelocity(velocity);
        world.spawnEntity(entity);
        return true;
    }
}
