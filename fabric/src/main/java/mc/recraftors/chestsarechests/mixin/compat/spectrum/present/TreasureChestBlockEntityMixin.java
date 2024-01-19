package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.chests.SpectrumChestBlockEntity;
import de.dafuqs.spectrum.blocks.structure.TreasureChestBlockEntity;
import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TreasureChestBlockEntity.class, remap = false)
public abstract class TreasureChestBlockEntityMixin extends SpectrumChestBlockEntity implements FallInContainer, LidFlingHelper {
    @Shadow private Identifier requiredAdvancementIdentifierToOpen;

    protected TreasureChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        if (this.requiredAdvancementIdentifierToOpen != null) return false;
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
    }

    @Override
    public VoxelShape chests$InputAreaShape() {
        return ABOVE;
    }

    @Override
    public int chests$xOff() {
        return 1;
    }
}
