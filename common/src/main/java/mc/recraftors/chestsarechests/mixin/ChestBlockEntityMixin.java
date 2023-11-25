package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.BooleanProvider;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean chests$tryInsertion(ItemEntity entity, BlockState state, BlockEntity block) {
        ItemStack stack = entity.getStack().copy();
        int size = this.size();
        boolean success = false;
        this.checkLootInteraction(null);
        DefaultedList<ItemStack> inv = getInvStackList();
        for (int t = 0; t < size && !stack.isEmpty(); t++) {
            ItemStack tStack = inv.get(t);
            if (tStack.isEmpty()) {
                setStack(t, stack);
                stack = ItemStack.EMPTY;
                success = true;
            } else if (ChestsAreChests.canMergeItems(stack, tStack)) {
                int capability = stack.getMaxCount() - tStack.getCount();
                int amount = Math.min(stack.getCount(), capability);
                if (amount > 0) {
                    stack.decrement(amount);
                    tStack.decrement(amount);
                    success = true;
                }
            }
        }
        if (success) {
            if (stack.isEmpty()) {
                entity.discard();
            } else {
                entity.setStack(stack);
            }
        }
        return success;
    }
}
