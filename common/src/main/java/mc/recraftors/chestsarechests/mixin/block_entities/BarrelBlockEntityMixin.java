package mc.recraftors.chestsarechests.mixin.block_entities;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer, Inventory {
    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    @Shadow private ViewerCountManager stateManager;

    protected BarrelBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @SuppressWarnings("DuplicatedCode")
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/ViewerCountManager;updateViewerCount(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void tickPostViewerCountUpdateInjector(CallbackInfo ci) {
        if (this.getWorld() == null || ! this.getWorld().getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        ServerWorld w = (ServerWorld) this.getWorld();
        BlockPos pos = this.getPos();
        BlockState state = w.getBlockState(pos);
        BlockOpenableContainer container = (BlockOpenableContainer) this.stateManager;
        if (this.chests$isOpen() && container.chests$isForcedOpened(w) && !container.chests$shouldStayOpen(w)){
            this.chests$forceClose();
        }
        if (!this.chests$isOpen()) return;
        if (!state.getProperties().contains(Properties.FACING)) return;
        Direction dir = state.get(Properties.FACING);
        if (dir == Direction.DOWN && w.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial())) {
            Vec3d center = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
            Box box = Box.of(center, 1, .5, 1).offset(0, -.75, 0);
            if (w.isSpaceEmpty(box)) {
                Vec3d outPos = center.add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
                Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
                this.chests$fallOut(w, dir, this, outPos, velocity);
                this.stateManager.updateViewerCount(world, pos, state);
            }
        }
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        if (this.world == null) return EMPTY;
        BlockState state = this.world.getBlockState(this.pos);
        if (!state.getProperties().contains(Properties.FACING)) return INSIDE;
        return DIRECTION_SHAPES.get(state.get(Properties.FACING));
    }

    @Override
    public boolean chests$isOpen() {
        if (this.world == null) return false;
        BlockState state = this.world.getBlockState(this.pos);
        if (!state.getProperties().contains(Properties.OPEN)) return false;
        return state.get(Properties.OPEN);
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return ((BlockOpenableContainer)this.stateManager).chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
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
        BlockOpenableContainer container = (BlockOpenableContainer) this.stateManager;
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(this.getWorld(), this.getPos());
        this.getWorld().setBlockState(this.getPos(), this.getWorld().getBlockState(this.getPos()).with(Properties.OPEN, false), Block.NOTIFY_LISTENERS);
        return true;
    }

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        this.checkLootInteraction(null);
        return FallInContainer.chests$inventoryInsertion(getInvStackList(), entity, this::setStack);
    }
}
