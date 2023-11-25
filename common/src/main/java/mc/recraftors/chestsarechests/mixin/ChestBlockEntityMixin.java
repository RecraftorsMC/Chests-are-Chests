package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.BooleanProvider;
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

    @Override
    public boolean chests$tryInsertion(ItemEntity entity) {
        this.checkLootInteraction(null);
        return FallInContainer.chests$inventoryInsertion(getInvStackList(), entity, this::setStack);
    }
}
