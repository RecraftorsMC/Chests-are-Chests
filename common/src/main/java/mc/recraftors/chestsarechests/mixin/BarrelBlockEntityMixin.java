package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer {
    @Shadow protected abstract DefaultedList<ItemStack> getInvStackList();

    protected BarrelBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
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
