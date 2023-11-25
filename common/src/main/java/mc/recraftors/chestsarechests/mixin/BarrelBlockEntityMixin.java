package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer {
    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    @Shadow private DefaultedList<ItemStack> inventory;

    @Shadow private ViewerCountManager stateManager;

    protected BarrelBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Unique
    private void chests$dropAllDown() {
        if (this.world == null) {
            return;
        }
        int s = this.inventory.size();
        for (int i = 0; i < s; i++) {
            ItemStack stack = this.inventory.get(i);
            if (stack.isEmpty()) continue;
            ItemEntity entity = new ItemEntity(this.world, this.pos.getX()+.5, this.pos.getY()-.5, this.pos.getZ()+.5, stack.copy());
            entity.setVelocity(0, -.05, 0);
            this.world.spawnEntity(entity);
            this.removeStack(i);
            this.markDirty();
        }
        this.stateManager.updateViewerCount(world, pos, world.getBlockState(pos));
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/ViewerCountManager;updateViewerCount(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void tickPostViewerCountUpdateInjector(CallbackInfo ci) {
        if (this.world == null || ! this.world.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        BlockState state = this.world.getBlockState(this.pos);
        if (!state.getProperties().contains(Properties.FACING)) return;
        if (state.get(Properties.FACING) == Direction.DOWN) {
            Box box = new Box(this.pos.getX(), this.pos.getY()-.5d, this.pos.getZ(), this.pos.getX()+1, this.pos.getY(), this.pos.getZ()+1);
            if (this.world.isSpaceEmpty(box)) {
                this.chests$dropAllDown();
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
    public boolean chests$tryInsertion(ItemEntity entity) {
        this.checkLootInteraction(null);
        return FallInContainer.chests$inventoryInsertion(getInvStackList(), entity, this::setStack);
    }
}
