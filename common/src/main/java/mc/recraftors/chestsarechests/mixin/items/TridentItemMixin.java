package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin implements ContainerItemHelper {
    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        TridentEntity entity = new TridentEntity(world, null, stack);
        entity.setPitch(-90f);
        entity.setVelocity(velocity.multiply(2));
        world.spawnEntity(entity);
        return true;
    }
}
