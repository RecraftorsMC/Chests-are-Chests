package mc.recraftors.chestsarechests.mixin.block_entities;

import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer {

    protected ChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow public abstract int size();

    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    @Shadow @Final private ChestLidAnimator lidAnimator;

    @Shadow @Final private ViewerCountManager stateManager;

    @Override
    public VoxelShape chests$InputAreaShape() {
        return ABOVE;
    }

    @Override
    public boolean chests$isOpen() {
        return this.chests$getBooleanHolder().chests$getBool();
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.chests$getBooleanHolder().chests$setBool(true);
    }

    @Override
    public boolean chests$forceClose() {
        BlockOpenableContainer container = this.chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(world, pos);
        this.chests$getBooleanHolder().chests$setBool(false);
        return true;
    }

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        this.checkLootInteraction(null);
        return FallInContainer.chests$inventoryInsertion(getInvStackList(), entity, this::setStack);
    }

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.stateManager;
    }

    @Override
    public @NotNull BooleanHolder chests$getBooleanHolder() {
        return (BooleanHolder) this.lidAnimator;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void chests$onTick() {
        if (!(this.getWorld() instanceof ServerWorld w)) {
            return;
        }
        BlockOpenableContainer container = this.chests$getContainer();
        if (this.chests$isOpen() && container.chests$isForcedOpened(w) && !container.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
        if (!this.chests$isOpen()) return;
        if (!w.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        if (!w.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial())) return;
        BlockPos pos = this.getPos();
        Vec3d center = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        Box box = Box.of(center, 1, .5, 1).offset(0, .75, 0);
        if (!w.isSpaceEmpty(box)) return;
        Direction dir = Direction.UP;
        Vec3d outPos = center.add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
        Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
        this.chests$fallOut(w, dir, this, outPos, velocity);
        this.stateManager.updateViewerCount(w, pos, w.getBlockState(pos));
    }

    @Inject(method = "onScheduledTick", at = @At("HEAD"))
    private void tickHeadInjector(CallbackInfo ci) {
        this.chests$onTick();
    }

    @Mixin(targets = "net/minecraft/block/entity/ChestBlockEntity$1")
    static class StateManagerMixin {
        @Shadow @Final ChestBlockEntity field_27211;

        @Inject(method = "onContainerOpen", at = @At("HEAD"))
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            ChestsAreChests.ejectAbove(direction, this.field_27211);
        }
    }
}
