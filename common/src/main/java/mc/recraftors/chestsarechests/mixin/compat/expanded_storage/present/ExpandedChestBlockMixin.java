package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.impl.block.ChestBlock;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public abstract class ExpandedChestBlockMixin {
    @Inject(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcompasses/expandedstorage/impl/block/entity/ChestBlockEntity;updateViewerCount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void onScheduledTickBeforeUpdateViewerCountInjector(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof FallInContainer fallIn)) return;
        fallIn.chests$onTick();
    }
}
