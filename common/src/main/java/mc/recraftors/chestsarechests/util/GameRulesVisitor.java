package mc.recraftors.chestsarechests.util;

import net.minecraft.world.GameRules;

public interface GameRulesVisitor {
    default void chests$visitFloat(GameRules.Key<FloatRule> key, GameRules.Type<FloatRule> type) {
    }
}
