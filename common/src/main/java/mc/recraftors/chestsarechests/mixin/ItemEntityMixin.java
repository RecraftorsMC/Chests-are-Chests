package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        if (!(this.getWorld() instanceof ServerWorld world)) return;
        if (!world.getGameRules().getBoolean(ChestsAreChests.getInsertOpen())) return;
        if (!this.horizontalCollision && !this.verticalCollision) return;
        boolean consumed = false;
        int x = (int)getX();
        int y = (int)getY();
        int z = (int)getZ();
        ItemEntity item = (ItemEntity) ((Object)this);
        BlockPos pos = new BlockPos(x, (int) (getY()-.5), z);
        BlockState state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        pos = new BlockPos(x, (int) (getY()+.5), z);
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        // proximity to block side bounds comparison -> dx < dz => try insert on X first
        double dx = Math.abs(1-(getX() - x));
        double dz = Math.abs(1-(getZ() - z));
        boolean axis = dx < dz;
        if (axis) {
            //noinspection DuplicatedCode
            pos = new BlockPos((int) (getX() + .5), y, z);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                consumed = container.chests$fallIn(pos, state, item);
            }
            if (consumed) return;
            //noinspection DuplicatedCode
            pos = new BlockPos((int) (getX() - .5), y, z);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                consumed = container.chests$fallIn(pos, state, item);
            }
            if (consumed) return;
        }
        pos = new BlockPos(x, y, (int) (getZ() + .5));
        //noinspection DuplicatedCode
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        pos = new BlockPos(x, y, (int) (getZ() - .5));
        //noinspection DuplicatedCode
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        if (!axis) {
            //noinspection DuplicatedCode
            pos = new BlockPos((int) (getX() + .5), y, z);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                consumed = container.chests$fallIn(pos, state, item);
            }
            if (consumed) return;
            //noinspection DuplicatedCode
            pos = new BlockPos((int) (getX() - .5), y, z);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                container.chests$fallIn(pos, state, item);
            }
        }
    }
}
