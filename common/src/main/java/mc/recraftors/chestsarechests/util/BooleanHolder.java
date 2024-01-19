package mc.recraftors.chestsarechests.util;

/**
 * Boolean attribute accessor interface.
 */
public interface BooleanHolder {
    default boolean chests$getBool() {
        return false;
    }

    default void chests$setBool(boolean b) {}
}
