package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.util.GameRulesVisitor;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRules.Visitor.class)
public interface GameRulesVisitorMixin extends GameRulesVisitor {
}
