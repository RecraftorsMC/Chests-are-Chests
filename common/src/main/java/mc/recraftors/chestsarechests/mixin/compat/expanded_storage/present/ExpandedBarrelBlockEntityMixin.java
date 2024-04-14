package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.impl.block.entity.BarrelBlockEntity;
import compasses.expandedstorage.impl.inventory.ExposedInventory;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BarrelBlockEntity.class)
public abstract class ExpandedBarrelBlockEntityMixin extends BlockEntity implements FallInContainer, ExposedInventory {

    @Shadow @Final private ViewerCountManager manager;

    ExpandedBarrelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.manager;
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        if (this.getWorld() == null) return EMPTY;
        BlockState state = this.getWorld().getBlockState(this.getPos());
        if (!state.getProperties().contains(Properties.FACING)) return INSIDE;
        return DIRECTION_SHAPES.get(state.get(Properties.FACING));
    }

    @Override
    public boolean chests$isOpen() {
        if (this.getWorld() == null) return false;
        BlockState state = this.getWorld().getBlockState(this.getPos());
        if (!state.getProperties().contains(Properties.OPEN)) return false;
        return state.get(Properties.OPEN);
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return chests$getContainer().chests$openContainerBlock((ServerWorld) this.getWorld(), this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        BlockState state = world.getBlockState(at);
        if (!state.getProperties().contains(Properties.OPEN)) return;
        world.setBlockState(at, state.with(Properties.OPEN, true), Block.NOTIFY_LISTENERS);
    }

    @Override
    public boolean chests$forceClose() {
        if (this.getWorld() == null) return false;
        BlockOpenableContainer container = chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(this.getWorld(), this.getPos());
        this.getWorld().setBlockState(this.getPos(), this.getWorld().getBlockState(this.getPos()).with(Properties.OPEN, false), Block.NOTIFY_LISTENERS);
        return true;
    }

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        return FallInContainer.chests$inventoryInsertion(getItems(), entity, this::setStack);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void chests$onTick() {
        World s = this.getWorld();
        if (!(s instanceof ServerWorld serverWorld)) {
            return;
        }
        BlockState state = serverWorld.getBlockState(this.getPos());
        BlockOpenableContainer chested = this.chests$getContainer();
        if (this.chests$isOpen() && chested.chests$isForcedOpened(serverWorld) && !chested.chests$shouldStayOpen(serverWorld)) {
            this.chests$forceClose();
        }
        if (!this.chests$isOpen()) return;
        if (!state.getProperties().contains(Properties.FACING)) return;
        if (!serverWorld.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        Direction dir = state.get(Properties.FACING);
        Box box = Box.of(pos.toCenterPos(), 1 - 0.5 * Math.abs(dir.getOffsetX()),
                        1 - 0.5 * Math.abs(dir.getOffsetY()), 1 - 0.5 * Math.abs(dir.getOffsetZ()))
                .offset(.75 * dir.getOffsetX(), .75 * dir.getOffsetY(), .75 * dir.getOffsetZ());
        if (serverWorld.isSpaceEmpty(box)) {
            Vec3d outPos = pos.toCenterPos().add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
            Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
            this.chests$fallOut(serverWorld, dir, this, outPos, velocity);
        }
    }
}
