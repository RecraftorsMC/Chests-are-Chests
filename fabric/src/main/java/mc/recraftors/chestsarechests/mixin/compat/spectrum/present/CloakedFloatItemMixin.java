package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.gravity.CloakedFloatItem;
import de.dafuqs.spectrum.blocks.gravity.FloatItem;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CloakedFloatItem.class, remap = false)
public abstract class CloakedFloatItemMixin extends FloatItem implements ContainerItemHelper {
    CloakedFloatItemMixin(Settings settings, float gravityMod) {
        super(settings, gravityMod);
    }

    @Override
    public Direction[] chests$getFallDirection(ItemStack stack) {
        return new Direction[]{(this.getGravityMod() < 0 ? Direction.DOWN : Direction.UP)};
    }

    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, ServerWorld world, Vec3d pos, Vec3d velocity) {
        float f = -(1 / this.getGravityMod());
        return ContainerItemHelper.defaultOnOpenTick(stack, container, direction, world, pos, velocity.multiply(1, f, 1));
    }
}
