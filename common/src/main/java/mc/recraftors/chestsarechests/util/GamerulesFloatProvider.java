package mc.recraftors.chestsarechests.util;

import net.minecraft.world.GameRules;

/**
 * Utility interface for floating gamerules handling.
 */
public interface GamerulesFloatProvider {
    float chests$getFloat(GameRules.Key<FloatRule> key);
}
