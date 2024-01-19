package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EggItem.class)
public abstract class EggItemMixin implements ContainerItemHelper {
    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        if (direction != chests$getFallDirection(stack)) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            EggEntity entity = new EggEntity(world, pos.x, pos.y, pos.z);
            entity.setVelocity(velocity.multiply(1d, 2d, 1d));
            world.spawnEntity(entity);
        }
        return true;
    }
}
