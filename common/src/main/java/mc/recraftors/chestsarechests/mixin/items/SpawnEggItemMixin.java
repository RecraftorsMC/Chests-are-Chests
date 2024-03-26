package mc.recraftors.chestsarechests.mixin.items;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin implements ContainerItemHelper {

    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        if (!ChestsAreChests.isInArray(direction, chests$getFallDirection(stack))) return false;
        SpawnEggItem eggItem = (SpawnEggItem) stack.getItem();
        EntityType<?> type = eggItem.getEntityType(stack.getNbt());
        if (type.isIn(ChestsAreChests.DOES_NOT_FALL_HATCH)) return false;
        for (int i = 0; i < stack.getCount(); i++) {
            Entity entity = type.spawnFromItemStack(world, stack, null, new BlockPos((int) pos.x, (int) pos.y, (int) pos.z), SpawnReason.SPAWN_EGG, false, false);
            if (entity == null) {
                return false;
            }
            entity.setVelocity(velocity);
        }
        return true;
    }
}
