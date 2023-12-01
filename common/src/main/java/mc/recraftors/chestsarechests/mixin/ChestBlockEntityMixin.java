package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanProvider;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
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
        return ((BooleanProvider)this.lidAnimator).chests$getBool();
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.lidAnimator.setOpen(true);
    }

    @Override
    public boolean chests$forceClose() {
        BlockOpenableContainer container = this.chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(world, pos);
        this.lidAnimator.setOpen(false);
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

    @Inject(method = "onScheduledTick", at = @At("HEAD"))
    private void tickHeadInjector(CallbackInfo ci) {
        ServerWorld w = (ServerWorld) this.getWorld();
        BlockOpenableContainer container = this.chests$getContainer();
        if (this.chests$isOpen() && container.chests$isForcedOpened(w) && !container.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
    }

    @Mixin(targets = "net/minecraft/block/entity/ChestBlockEntity$1")
    static class StateManagerMixin {
        @Shadow @Final ChestBlockEntity field_27211;

        @Inject(method = "onContainerOpen", at = @At("HEAD"))
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!world.getGameRules().getBoolean(ChestsAreChests.getLidFling())) return;
            if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            world.getEntitiesByType(EntityType.ITEM, new Box(this.field_27211.getPos().offset(Direction.UP, 1)), t -> true).forEach(e -> {
                ChestsAreChests.lidFlingItem(e, direction);
            });
        }
    }
}
