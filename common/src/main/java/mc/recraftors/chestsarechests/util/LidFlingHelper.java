package mc.recraftors.chestsarechests.util;

public interface LidFlingHelper {
    default float chests$verticalFactor() {
        return 1;
    }

    default float chests$horizontalFactor() {
        return 1;
    }

    default int chests$xOff() {
        return 0;
    }

    default int chests$yOff() {
        return 0;
    }

    default int chests$zOff() {
        return 0;
    }
}
