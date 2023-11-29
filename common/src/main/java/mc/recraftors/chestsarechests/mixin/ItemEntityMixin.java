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
import net.minecraft.util.math.Direction;
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
        ItemEntity item = (ItemEntity) ((Object)this);
        // inside
        BlockPos pos = item.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        // below
        pos = pos.down();
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        // up
        pos = pos.up(2);
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        // proximity to block side bounds comparison -> dx < dz => try insert on X first
        double dx = Math.abs(1-(getX() - (int)getX()));
        double dz = Math.abs(1-(getZ() - (int)getZ()));
        boolean axis = dx < dz;
        if (axis) {
            // east
            //noinspection DuplicatedCode
            pos = item.getBlockPos().offset(Direction.Axis.X, 1);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                consumed = container.chests$fallIn(pos, state, item);
            }
            if (consumed) return;
            // west
            pos = pos.offset(Direction.Axis.X, -2);
            state = world.getBlockState(pos);
            if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
                consumed = container.chests$fallIn(pos, state, item);
            }
            if (consumed) return;
        }
        // south
        pos = item.getBlockPos().offset(Direction.Axis.Z, 1);
        //noinspection DuplicatedCode
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        pos = pos.offset(Direction.Axis.Z, -2);
        //noinspection DuplicatedCode
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed || !axis) {
            return;
        }
        // east
        //noinspection DuplicatedCode
        pos = item.getBlockPos().offset(Direction.Axis.X, 1);
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            consumed = container.chests$fallIn(pos, state, item);
        }
        if (consumed) return;
        // west
        pos = pos.offset(Direction.Axis.X, -2);
        state = world.getBlockState(pos);
        if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof FallInContainer container) {
            container.chests$fallIn(pos, state, item);
        }
    }
}
