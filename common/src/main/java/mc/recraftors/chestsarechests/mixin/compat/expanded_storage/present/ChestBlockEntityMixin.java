package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.common.block.entity.ChestBlockEntity;
import compasses.expandedstorage.common.block.entity.OldChestBlockEntity;
import compasses.expandedstorage.common.block.entity.extendable.OpenableBlockEntity;
import compasses.expandedstorage.common.block.strategies.ItemAccess;
import compasses.expandedstorage.common.block.strategies.Lockable;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends OldChestBlockEntity implements FallInContainer, BooleanHolder {
    @Shadow @Final private ChestLidAnimator lidController;

    @Shadow @Final private ViewerCountManager manager;

    ChestBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state, Identifier blockId, Function<OpenableBlockEntity, ItemAccess> access, Supplier<Lockable> lockable) {
        super(type, pos, state, blockId, access, lockable);
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
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) c$e$g().getWorld(), c$e$g().getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.chests$getBooleanHolder().chests$setBool(true);
    }

    @Override
    public boolean chests$forceClose() {
        BlockOpenableContainer container = this.chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) c$e$g().getWorld())) return false;
        container.chests$forceClose(c$e$g().getWorld(), c$e$g().getPos());
        this.chests$getBooleanHolder().chests$setBool(false);
        return true;
    }

    @Override
    @SuppressWarnings("RedundantCast")
    public boolean chests$tryInsertion(ItemEntity entity) {
        Inventory i = (Inventory) this;
        return FallInContainer.chests$inventoryInsertion(getItems(), entity, i::setStack);
    }

    @Mixin(targets = "compasses/expandedstorage/common/block/entity/ChestBlockEntity$1")
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
