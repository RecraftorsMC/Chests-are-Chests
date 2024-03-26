package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SnowballItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SnowballItem.class)
public abstract class SnowballItemMixin implements ContainerItemHelper {
    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            SnowballEntity entity = new SnowballEntity(world, pos.x, pos.y, pos.z);
            entity.setVelocity(velocity.multiply(1, 2, 1));
            world.spawnEntity(entity);
        }
        return true;
    }
}
