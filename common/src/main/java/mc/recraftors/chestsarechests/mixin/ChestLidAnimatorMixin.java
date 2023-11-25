package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.BooleanProvider;
import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChestLidAnimator.class)
public abstract class ChestLidAnimatorMixin implements BooleanProvider {
    @Shadow private boolean open;

    @Override
    public boolean chests$getBool() {
        return open;
    }
}
