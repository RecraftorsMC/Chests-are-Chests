package mc.recraftors.chestsarechests.util;

import net.minecraft.world.GameRules;

public interface GamerulesFloatProvider {
    float chests$getFloat(GameRules.Key<FloatRule> key);
}
