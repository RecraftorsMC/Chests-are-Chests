package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowItem.class)
public abstract class ArrowItemMixin implements ContainerItemHelper {
    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            ArrowEntity entity = new ArrowEntity(world, pos.x, pos.y, pos.z);
            entity.initFromStack(stack);
            entity.setPitch(-90f);
            entity.setVelocity(velocity.multiply(0, 4, 0));
            world.spawnEntity(entity);
        }
        return true;
    }
}
