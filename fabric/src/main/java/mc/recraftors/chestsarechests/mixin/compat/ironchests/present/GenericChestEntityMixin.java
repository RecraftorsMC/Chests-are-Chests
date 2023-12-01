package mc.recraftors.chestsarechests.mixin.compat.ironchests.present;

import io.github.cyberanner.ironchests.blocks.blockentities.GenericChestEntity;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.BlockOpenableContainer;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashSet;

@Mixin(GenericChestEntity.class)
public abstract class GenericChestEntityMixin extends ChestBlockEntity implements FallInContainer, BlockOpenableContainer {
    @Shadow(remap=false) int viewerCount;

    @Shadow public abstract void markDirty();

    @Unique private int chests$blockOpenTick = 0;
    @Unique private boolean chests$blockForcedOpen = false;
    @Unique
    private final Collection<ServerPlayerEntity> chests$viewers = new HashSet<>();

    protected GenericChestEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public boolean chests$isOpen() {
        return this.viewerCount > 0;
    }

    @Override
    public Collection<ServerPlayerEntity> chests$getViewers() {
        return this.chests$viewers;
    }

    @Override
    public boolean chests$openContainerBlock(ServerWorld world, BlockPos pos, BlockState from, FallInContainer container) {
        if (this.chests$shouldStayOpen(world)) return false;
        int duration = Math.max(1, world.getGameRules().getInt(ChestsAreChests.getDispenserOpenDuration()));
        this.chests$blockOpenTick = world.getServer().getTicks() + duration;
        this.chests$blockForcedOpen = true;
        container.chests$forceOpen(world, pos, from);
        ChestsAreChests.scheduleTick(world, pos, duration);
        return true;
    }

    @Override
    public boolean chests$shouldStayOpen(ServerWorld world) {
        if (this.chests$blockOpenTick <= world.getServer().getTicks()) this.chests$blockForcedOpen = false;
        return this.chests$blockForcedOpen;
    }

    @Override
    public boolean chests$tryForceOpen(BlockState from) {
        return this.chests$openContainerBlock((ServerWorld) this.world, this.getPos(), from, this);
    }

    @Override
    public void chests$forceOpen(ServerWorld world, BlockPos at, BlockState from) {
        this.viewerCount++;
        BlockState state = world.getBlockState(this.getPos());
        if (state.getProperties().contains(HorizontalFacingBlock.FACING)) {
            Direction direction = state.get(HorizontalFacingBlock.FACING);
            ChestsAreChests.ejectAbove(direction, this);
        }
        this.markDirty();
    }

    @Override
    public boolean chests$forceClose() {
        if (this.chests$shouldStayOpen((ServerWorld) this.getWorld())) return false;
        this.chests$forceClose(this.getWorld(), this.getPos());
        return true;
    }

    @Override
    public void chests$forceClose(World world, BlockPos pos) {
        for (ServerPlayerEntity player : this.chests$getViewers()) {
            player.closeHandledScreen();
        }
        this.viewerCount = 0;
        this.markDirty();
    }

    @Override
    public boolean chests$isForcedOpened(ServerWorld world) {
        return chests$blockForcedOpen;
    }

    @Override
    public void onScheduledTick() {
        super.onScheduledTick();
        ServerWorld w = (ServerWorld) this.getWorld();
        if (this.chests$isOpen() && this.chests$isForcedOpened(w) && !this.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
    }

    @Inject(method = "onOpen", at = @At("HEAD"))
    private void onOpenHeadInjector(PlayerEntity player, CallbackInfo ci) {
        if (this.viewerCount < 0) this.viewerCount = 0;
    }

    @Inject(method = "onOpen", at = @At(value = "INVOKE", target = "Lio/github/cyberanner/ironchests/blocks/blockentities/GenericChestEntity;markDirty()V"))
    private void onOpenMarkDirtyInjector(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            this.chests$viewers.add(serverPlayer);
        }
        if (world == null) return;
        BlockState state = world.getBlockState(this.getPos());
        if (!state.getProperties().contains(HorizontalFacingBlock.FACING)) return;
        Direction direction = state.get(HorizontalFacingBlock.FACING);
        ChestsAreChests.ejectAbove(direction, this);
    }
}
