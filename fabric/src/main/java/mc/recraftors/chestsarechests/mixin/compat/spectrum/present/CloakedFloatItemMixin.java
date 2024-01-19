package mc.recraftors.chestsarechests.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.blocks.gravity.CloakedFloatItem;
import de.dafuqs.spectrum.blocks.gravity.FloatItem;
import mc.recraftors.chestsarechests.util.ContainerItemHelper;
import mc.recraftors.chestsarechests.util.FallInContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CloakedFloatItem.class, remap = false)
public abstract class CloakedFloatItemMixin extends FloatItem implements ContainerItemHelper {
    CloakedFloatItemMixin(Settings settings, float gravityMod) {
        super(settings, gravityMod);
    }

    @Override
    public Direction[] chests$getFallDirection(ItemStack stack) {
        return new Direction[]{(this.getGravityModForItemEntity() < 0 ? Direction.DOWN : Direction.UP)};
    }

    @Override
    public boolean chests$onOpenTick(ItemStack stack, FallInContainer container, Direction direction, World world, Vec3d pos, Vec3d velocity) {
        float f = (float) -(1 / this.getGravityModForItemEntity());
        return ContainerItemHelper.super.chests$onOpenTick(stack, container, direction, world, pos, velocity.multiply(1, f, 1));
    }
}
