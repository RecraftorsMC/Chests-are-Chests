package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.impl.block.entity.ChestBlockEntity;
import compasses.expandedstorage.impl.block.entity.OldChestBlockEntity;
import compasses.expandedstorage.impl.block.entity.extendable.OpenableBlockEntity;
import compasses.expandedstorage.impl.block.strategies.ItemAccess;
import compasses.expandedstorage.impl.block.strategies.Lockable;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChestBlockEntity.class)
public abstract class ExpandedChestBlockEntityMixin extends OldChestBlockEntity implements FallInContainer, BooleanHolder {
    @Shadow @Final private ChestLidAnimator lidController;

    @Shadow @Final private ViewerCountManager manager;

    ExpandedChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state, Identifier blockId, Function<OpenableBlockEntity, ItemAccess> access, Supplier<Lockable> lockable) {
        super(type, pos, state, blockId, access, lockable);
    }

    @Override
    public boolean chests$getBool() {
        return isDinnerbone();
    }

    @Override
    public @NotNull BooleanHolder chests$getBooleanHolder() {
        return (BooleanHolder) this.lidController;
    }

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.manager;
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        return this.isDinnerbone() ? BELOW : ABOVE;
    }

    @Override
    public boolean chests$isOpen() {
        return chests$getBooleanHolder().chests$getBool();
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) this.getWorld(), this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.chests$getBooleanHolder().chests$setBool(true);
    }

    @Override
    public boolean chests$forceClose() {
        BlockOpenableContainer container = this.chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(this.getWorld(), this.getPos());
        this.chests$getBooleanHolder().chests$setBool(false);
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
        BlockOpenableContainer c = this.chests$getContainer();
        if (this.chests$isOpen() && c.chests$isForcedOpened(serverWorld) && !c.chests$shouldStayOpen(serverWorld)) {
            this.chests$forceClose();
        }
        if (!this.chests$isOpen()) return;
        if (!serverWorld.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        if (!serverWorld.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial())) return;
        Direction dir = this.chests$getBool() ? Direction.DOWN : Direction.UP;
        Box box = Box.of(pos.toCenterPos(), 1, .5, 1)
                .offset(.75 * dir.getOffsetX(), .75 * dir.getOffsetY(), .75 * dir.getOffsetZ());
        if (serverWorld.isSpaceEmpty(box)) {
            Vec3d outPos = pos.toCenterPos().add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
            Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
            this.chests$fallOut(serverWorld, dir, this, outPos, velocity);
        }
    }

    @Mixin(targets = "compasses/expandedstorage/impl/block/entity/ChestBlockEntity$1")
    static class ManagerMixin {
        @Shadow(remap = false) @Final ChestBlockEntity this$0;

        @Inject(method = "onContainerOpen", at = @At("HEAD"))
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            if (this$0.isDinnerbone()) {
                ChestsAreChests.ejectBelow(direction, this$0);
            } else {
                ChestsAreChests.ejectAbove(direction, this$0);
            }
        }
    }
}
