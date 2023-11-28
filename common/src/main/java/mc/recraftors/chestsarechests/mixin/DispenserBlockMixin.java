package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Shadow
    public static Position getOutputLocation(BlockPointer pointer) {
        return null;
    }

    @Inject(
            method = "dispense",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;syncWorldEvent(ILnet/minecraft/util/math/BlockPos;I)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onDispenseFailBeforeInjector(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (!world.getGameRules().getBoolean(ChestsAreChests.getDispenserOpen())) return;
        BlockPointer pointer = new BlockPointerImpl(world, pos);
        BlockState state = pointer.getBlockState();
        Direction direction = state.get(Properties.FACING);
        BlockPos target = pos.offset(direction, 1);
        BlockEntity entity = world.getBlockEntity(target);
        if (entity == null) {
            return;
        }
        if (!(entity instanceof FallInContainer container)) return;
        if ((container.chests$isOpen() && container.chests$forceClose()) || container.chests$tryForceOpen(state)) {
            pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
            pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, direction.getId());
            ci.cancel();
        }
    }
}
