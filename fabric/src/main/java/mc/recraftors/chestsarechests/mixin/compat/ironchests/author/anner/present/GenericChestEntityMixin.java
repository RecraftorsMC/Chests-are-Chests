package mc.recraftors.chestsarechests.mixin.compat.ironchests.author.anner.present;

import anner.ironchest.blocks.blockentities.GenericChestEntity;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
        ChestsAreChests.scheduleTick(world, pos, 0);
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
        chests$onTick();
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void chests$onTick() {
        ServerWorld w = (ServerWorld) this.getWorld();
        if (w == null) return;
        if (!this.chests$isOpen()) return;
        if (this.chests$isForcedOpened(w) && !this.chests$shouldStayOpen(w)) {
            this.chests$forceClose();
        }
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

    @Inject(method = "onOpen", at = @At("HEAD"))
    private void onOpenHeadInjector(PlayerEntity player, CallbackInfo ci) {
        if (this.viewerCount < 0) this.viewerCount = 0;
    }

    @Inject(method = "onOpen", at = @At(value = "INVOKE", target = "Lanner/ironchest/blocks/blockentities/GenericChestEntity;markDirty()V"))
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
