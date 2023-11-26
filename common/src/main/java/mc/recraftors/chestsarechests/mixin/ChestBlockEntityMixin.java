package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.BooleanProvider;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
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

    @Override
    public VoxelShape chests$InputAreaShape() {
        return ABOVE;
    }

    @Override
    public boolean chests$isOpen() {
        return ((BooleanProvider)this.lidAnimator).chests$getBool();
    }

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        this.checkLootInteraction(null);
        return FallInContainer.chests$inventoryInsertion(getInvStackList(), entity, this::setStack);
    }

    @Mixin(targets = "net/minecraft/block/entity/ChestBlockEntity$1")
    private static class StateManagerMixin {
        @Shadow @Final ChestBlockEntity field_27211;

        @Inject(method = "onContainerOpen", at = @At("HEAD"))
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!world.getGameRules().getBoolean(ChestsAreChests.getLidFling())) return;
            if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            world.getEntitiesByType(EntityType.ITEM, new Box(this.field_27211.getPos().offset(Direction.UP, 1)), t -> true).forEach(e -> {
                int x = direction.getOffsetX();
                int z = direction.getOffsetZ();
                e.addVelocity(-0.25 * x, .6, -0.25*z);
            });
        }
    }
}
