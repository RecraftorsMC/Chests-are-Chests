package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.ChestsAreChests;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

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
        if (ChestsAreChests.automatedContainerOpening(world, pos, state, direction)) {
            pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
            pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, direction.getId());
            ci.cancel();
        }
    }
}
