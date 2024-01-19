package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.chests.HeartboundChestBlockEntity;
import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = HeartboundChestBlockEntity.class, remap = false)
public abstract class HeartBoundChestBlockEntityMixin implements FallInContainer, LidFlingHelper {
    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return FallInContainer.super.chests$tryForceOpen(from);
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        return ABOVE;
    }

    @Override
    public int chests$yOff() {
        return 1;
    }
}
