package mc.recraftors.chestsarechests.mixin.compat.easy_villagers;

import de.maxhenkel.easyvillagers.entity.EasyVillagerEntity;
import de.maxhenkel.easyvillagers.items.VillagerItem;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerItem.class)
public abstract class VillagerItemMixin implements ContainerItemHelper {
    @Shadow public abstract EasyVillagerEntity getVillager(World world, ItemStack stack);

    @Override
    public Direction[] chests$getFallDirection(ItemStack stack) {
        return new Direction[]{Direction.DOWN, Direction.UP};
    }

    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            EasyVillagerEntity villager = this.getVillager(world, stack);
            villager.updatePosition(pos.x, pos.y, pos.z);
            villager.setVelocity(velocity);
            world.spawnEntity(villager);
        }
        return true;
    }
}
