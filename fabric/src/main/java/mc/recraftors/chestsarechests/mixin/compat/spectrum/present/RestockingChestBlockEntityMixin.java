package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.chests.RestockingChestBlockEntity;
import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RestockingChestBlockEntity.class, remap = false)
public abstract class RestockingChestBlockEntityMixin implements FallInContainer, LidFlingHelper {
    @Override
    public VoxelShape chests$InputAreaShape() {
        return VoxelShapes.union(
                Block.createCuboidShape(0, 10, 0, 16, 16, 2),
                Block.createCuboidShape(0, 10, 0, 2, 16, 16),
                Block.createCuboidShape(14, 10, 2, 16, 16, 16),
                Block.createCuboidShape(2, 10, 14, 16, 16, 16)
        );
    }

    @Override
    public float chests$horizontalFactor() {
        return 0;
    }
}
