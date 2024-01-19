package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.chests.CompactingChestBlockEntity;
import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CompactingChestBlockEntity.class, remap = false)
public abstract class CompactingChestBlockEntityMixin implements FallInContainer, LidFlingHelper {
    @Override
    public VoxelShape chests$InputAreaShape() {
        return VoxelShapes.union(
                Block.createCuboidShape(-2, 13, -2, 18, 16, 0),
                Block.createCuboidShape(-2, 13, -2, 0, 16, 18),
                Block.createCuboidShape(-2, 13, 16, 18, 16, 18),
                Block.createCuboidShape(16, 13, -2, 18, 16, 18)
        );
    }

    @Override
    public float chests$horizontalFactor() {
        return 0;
    }

    @Override
    public int chests$xOff() {
        return 1;
    }
}
