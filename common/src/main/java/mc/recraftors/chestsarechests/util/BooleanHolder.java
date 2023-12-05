package mc.recraftors.chestsarechests.util;

public interface BooleanHolder {
    default boolean chests$getBool() {
        return false;
    }

    default void chests$setBool(boolean b) {}
}
