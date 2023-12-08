package mc.recraftors.chestsarechests.mixin.compat.expanded_storage.present;

import compasses.expandedstorage.common.block.ChestBlock;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.BooleanHolder;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {
    @Inject(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcompasses/expandedstorage/common/block/entity/ChestBlockEntity;updateViewerCount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void onScheduledTickBeforeUpdateViewerCountInjector(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockEntity entity = world.getBlockEntity(pos);
        FallInContainer fallIn = (FallInContainer) entity;
        BlockOpenableContainer c = fallIn.chests$getContainer();
        if (!(fallIn instanceof BooleanHolder holder)) return;
        if (fallIn.chests$isOpen() && c.chests$isForcedOpened(world) && !c.chests$shouldStayOpen(world)) {
            fallIn.chests$forceClose();
        }
        if (!fallIn.chests$isOpen() || !holder.chests$getBool()) return;
        ChestsAreChests.dropAllDown((Inventory) entity, entity);
    }
}
