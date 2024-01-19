package mc.recraftors.chestsarechests.mixin.block_entities;

import mc.recraftors.chestsarechests.util.BooleanHolder;
import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChestLidAnimator.class)
public abstract class ChestLidAnimatorMixin implements BooleanHolder {
    @Shadow private boolean open;

    @Shadow public abstract void setOpen(boolean open);

    @Override
    public boolean chests$getBool() {
        return open;
    }

    @Override
    public void chests$setBool(boolean b) {
        this.setOpen(b);
    }
}
