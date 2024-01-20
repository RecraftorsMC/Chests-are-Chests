package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.chests.SpectrumChestBlock;
import de.dafuqs.spectrum.blocks.chests.SpectrumChestBlockEntity;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpectrumChestBlockEntity.class, remap = false)
public abstract class SpectrumChestBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer, LidFlingHelper {
    @Shadow @Final public ViewerCountManager stateManager;

    @Shadow @Final protected ChestLidAnimator lidAnimator;

    protected SpectrumChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.stateManager;
    }

    @Override
    public @NotNull BooleanHolder chests$getBooleanHolder() {
        return (BooleanHolder) this.lidAnimator;
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return this.chests$getContainer().chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.chests$getBooleanHolder().chests$setBool(true);
    }

    @Override
    public boolean chests$forceClose() {
        BlockOpenableContainer container = this.chests$getContainer();
        if (container.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        container.chests$forceClose(world, pos);
        this.chests$getBooleanHolder().chests$setBool(false);
        return true;
    }

    @SuppressWarnings("DuplicatedCode")
    @Inject(method = "onScheduledTick", at = @At("HEAD"))
    private void tickHeadInjector(CallbackInfo ci) {
        ServerWorld w = (ServerWorld) this.getWorld();
        BlockOpenableContainer container = this.chests$getContainer();
        if (this.chests$isOpen() && container.chests$isForcedOpened(w) && !container.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
        if (w == null) return;
        if (!this.chests$isOpen()) return;
        if (!w.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        if (!w.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial())) return;
        BlockPos pos = this.getPos();
        Box box = Box.of(pos.toCenterPos(), 1, .5, 1).offset(0, .75, 0);
        if (!w.isSpaceEmpty(box)) return;
        Direction dir = Direction.UP;
        Vec3d outPos = pos.toCenterPos().add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
        Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
        this.chests$fallOut(w, dir, this, outPos, velocity);
    }

    @Mixin(targets = "de/dafuqs/spectrum/blocks/chests/SpectrumChestBlockEntity$1", remap = false)
    static class StateManagerMixin {
        @Shadow @Final SpectrumChestBlockEntity this$0;

        @Inject(method = "onContainerOpen", at = @At("HEAD"), remap = true)
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!state.getProperties().contains(SpectrumChestBlock.FACING)) return;
            Direction direction = state.get(SpectrumChestBlock.FACING).getOpposite();
            ChestsAreChests.eject(direction, this$0, (LidFlingHelper) this$0);
        }
    }
}
