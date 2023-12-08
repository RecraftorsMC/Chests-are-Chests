package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.common.block.entity.BarrelBlockEntity;
import compasses.expandedstorage.common.block.entity.extendable.ExposedInventoryBlockEntity;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends ExposedInventoryBlockEntity implements FallInContainer {

    @Shadow @Final private ViewerCountManager manager;

    BarrelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state, Identifier blockId, Text defaultName, int inventorySize) {
        super(type, pos, state, blockId, defaultName, inventorySize);
    }

    /**
     * Provides this, cast as a BlockEntity.
     * <p>
     * This is just ugly, but if this is what it takes to make it work...
     * @return this, cast as a BlockEntity
     */
    @Unique
    @SuppressWarnings("RedundantCast")
    private BlockEntity c$e$g() {
        return (BlockEntity) this;
    }

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.manager;
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        if (c$e$g().getWorld() == null) return EMPTY;
        BlockState state = c$e$g().getWorld().getBlockState(c$e$g().getPos());
        if (!state.getProperties().contains(Properties.FACING)) return INSIDE;
        return DIRECTION_SHAPES.get(state.get(Properties.FACING));
    }

    @Override
    public boolean chests$isOpen() {
        if (c$e$g().getWorld() == null) return false;
        BlockState state = c$e$g().getWorld().getBlockState(c$e$g().getPos());
        if (!state.getProperties().contains(Properties.OPEN)) return false;
        return state.get(Properties.OPEN);
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return chests$getContainer().chests$openContainerBlock((ServerWorld) c$e$g().getWorld(), c$e$g().getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        BlockState state = world.getBlockState(at);
        if (!state.getProperties().contains(Properties.OPEN)) return;
        world.setBlockState(at, state.with(Properties.OPEN, true), Block.NOTIFY_LISTENERS);
    }

    @Override
    public boolean chests$forceClose() {
        BlockEntity e = c$e$g();
        if (e.getWorld() == null) return false;
        BlockOpenableContainer container = chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) e.getWorld())) return false;
        container.chests$forceClose(e.getWorld(), e.getPos());
        e.getWorld().setBlockState(e.getPos(), e.getWorld().getBlockState(e.getPos()).with(Properties.OPEN, false), Block.NOTIFY_LISTENERS);
        return true;
    }

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        return FallInContainer.chests$inventoryInsertion(getItems(), entity, this::setStack);
    }
}
