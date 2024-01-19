package mc.recraftors.chestsarechests.util;

import net.minecraft.world.GameRules;

/**
 * Utility interface to help with custom gamerule type handling.
 */
public interface GameRulesVisitor {
    default void chests$visitFloat(GameRules.Key<FloatRule> key, GameRules.Type<FloatRule> type) {
    }
}
