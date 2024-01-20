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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {
    @SuppressWarnings("DuplicatedCode")
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
        if (!(entity instanceof FallInContainer fallIn)) return;
        BlockOpenableContainer c = fallIn.chests$getContainer();
        if (!(fallIn instanceof BooleanHolder holder)) return;
        if (fallIn.chests$isOpen() && c.chests$isForcedOpened(world) && !c.chests$shouldStayOpen(world)) {
            fallIn.chests$forceClose();
        }
        if (!fallIn.chests$isOpen()) return;
        if (!world.getGameRules().getBoolean(ChestsAreChests.getBarrelFall())) return;
        if (!world.getGameRules().getBoolean(ChestsAreChests.getBarrelFallThrowableSpecial())) return;
        Direction dir = holder.chests$getBool() ? Direction.DOWN : Direction.UP;
        Box box = Box.of(pos.toCenterPos(), 1, .5, 1)
                .offset(.75 * dir.getOffsetX(), .75 * dir.getOffsetY(), .75 * dir.getOffsetZ());
        if (world.isSpaceEmpty(box)) {
            Vec3d outPos = pos.toCenterPos().add(0.75 * dir.getOffsetX(), 0.75 * dir.getOffsetY(), 0.75 * dir.getOffsetZ());
            Vec3d velocity = new Vec3d(0.05 * dir.getOffsetX(), 0.05 * dir.getOffsetY(), 0.05 * dir.getOffsetZ());
            fallIn.chests$fallOut(world, dir, (Inventory) entity, outPos, velocity);
        }
    }
}
