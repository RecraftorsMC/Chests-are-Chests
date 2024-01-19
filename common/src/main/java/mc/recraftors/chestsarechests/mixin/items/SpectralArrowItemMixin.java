package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpectralArrowItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpectralArrowItem.class)
public abstract class SpectralArrowItemMixin implements ContainerItemHelper {
    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            SpectralArrowEntity entity = new SpectralArrowEntity(world, pos.x, pos.y, pos.z);
            entity.setVelocity(velocity.multiply(1, 4, 1));
            world.spawnEntity(entity);
        }
        return true;
    }
}
