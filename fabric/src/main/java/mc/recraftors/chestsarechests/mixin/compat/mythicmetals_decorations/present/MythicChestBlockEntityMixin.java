package mc.recraftors.chestsarechests.mixin.compat.mythicmetals_decorations.present;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nourl.mythicmetalsdecorations.blocks.chest.MythicChestBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MythicChestBlockEntity.class, priority = 1001)
public abstract class MythicChestBlockEntityMixin extends LootableContainerBlockEntity implements FallInContainer {

    @Shadow(remap = false) @Final private ViewerCountManager stateManager;

    @Shadow(remap = false) @Final private ChestLidAnimator lidAnimator;

    @Override
    public @NotNull BlockOpenableContainer chests$getContainer() {
        return (BlockOpenableContainer) this.stateManager;
    }

    @Override
    public @Nullable BooleanHolder chests$getBooleanHolder() {
        return (BooleanHolder) this.lidAnimator;
    }

    protected MythicChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "onScheduledTick", at = @At("HEAD"))
    private void tickHeadInjector(CallbackInfo ci) {
        ServerWorld w = (ServerWorld) this.getWorld();
        BlockOpenableContainer container = this.chests$getContainer();
        if (this.chests$isOpen() && container.chests$isForcedOpened(w) && !container.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
    }

    @Mixin(targets = "nourl/mythicmetalsdecorations/blocks/chest/MythicChestBlockEntity$1")
    static class StateManagerMixin {
        @Shadow(remap = false) @Final MythicChestBlockEntity this$0;

        @Inject(method = "onContainerOpen", at = @At("HEAD"))
        private void onContainerOpenHeadInjector(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
            if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            ChestsAreChests.ejectAbove(direction, this.this$0);
        }
    }
}
