package mc.recraftors.chestsarechests.mixin.block_entities;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashSet;

@Mixin(ViewerCountManager.class)
public abstract class ViewerCountManagerMixin implements BlockOpenableContainer {
    @Shadow protected abstract void onContainerOpen(World world, BlockPos pos, BlockState state);

    @Shadow protected abstract void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount);

    @Shadow protected abstract void onContainerClose(World world, BlockPos pos, BlockState state);

    @Shadow private int viewerCount;
    @Unique private int chests$blockOpenTick = 0;
    @Unique private boolean chests$blockForcedOpen = false;
    @Unique private final Collection<ServerPlayerEntity> chests$viewers = new HashSet<>();

    @Override
    public boolean chests$openContainerBlock(ServerWorld world, BlockPos pos, BlockState from, FallInContainer container) {
        if (chests$shouldStayOpen(world)) return false;
        int duration = Math.max(1, world.getGameRules().getInt(ChestsAreChests.getDispenserOpenDuration()));
        this.chests$blockOpenTick = world.getServer().getTicks() + duration;
        this.chests$blockForcedOpen = true;
        BlockState it = world.getBlockState(pos);
        container.chests$forceOpen(world, pos, from);
        this.onContainerOpen(world, pos, it);
        this.onViewerCountUpdate(world, pos, it, 0, 1);
        ChestsAreChests.scheduleTick(world, pos, duration);
        return true;
    }

    @Override
    public boolean chests$shouldStayOpen(ServerWorld world) {
        if (this.chests$blockOpenTick <= world.getServer().getTicks()) this.chests$blockForcedOpen = false;
        return this.chests$blockForcedOpen;
    }

    @Override
    public void chests$forceClose(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        this.chests$blockOpenTick = 0;
        for (ServerPlayerEntity player : this.chests$getViewers()) {
            player.closeHandledScreen();
        }
        this.onContainerClose(world, pos, state);
        this.onViewerCountUpdate(world, pos, state, 0, 1);
    }

    @Override
    public Collection<ServerPlayerEntity> chests$getViewers() {
        return this.chests$viewers;
    }

    @Override
    public boolean chests$isForcedOpened(ServerWorld world) {
        return chests$blockForcedOpen;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Inject(method = "closeContainer", at = @At("HEAD"))
    private void closeContainerHeadInjector(PlayerEntity player, World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (this.viewerCount <= 0) {
            this.viewerCount = 1;
        }
        this.chests$getViewers().remove(player);
    }

    @Inject(method = "openContainer", at = @At("HEAD"))
    private void openContainerHeadInjector(PlayerEntity player, World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.chests$getViewers().add((ServerPlayerEntity) player);
    }

    @Inject(method = "openContainer", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/ViewerCountManager;onContainerOpen(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.AFTER))
    private void openContainerPostOnContainerOpenInjector(PlayerEntity player, World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.chests$blockForcedOpen = false;
    }
}
