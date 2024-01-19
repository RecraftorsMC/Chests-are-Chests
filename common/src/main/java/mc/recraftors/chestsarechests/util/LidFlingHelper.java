package mc.recraftors.chestsarechests.util;

/**
 * Utility class for chest lid item flinging.
 */
public interface LidFlingHelper {
    /**
     * Returns the current ejection vertical factor.
     * @return The current ejection vertical factor.
     */
    default float chests$verticalFactor() {
        return 1;
    }

    /**
     * Returns the current ejection horizontal factor.
     * @return The current ejection horizontal factor.
     */
    default float chests$horizontalFactor() {
        return 1;
    }

    /**
     * Returns the current ejection position offset on the X axis.
     * Allows to determine the block pos from which to eject all item.
     * @return The current ejection position offset on the X axis.
     */
    default int chests$xOff() {
        return 0;
    }

    /**
     * Returns the current ejection position offset on the Y axis.
     * Allows to determine the block pos from which to eject all item.
     * @return The current ejection position offset on the Y axis.
     */
    default int chests$yOff() {
        return 0;
    }

    /**
     * Returns the current ejection position offset on the Z axis.
     * Allows to determine the block pos from which to eject all item.
     * @return The current ejection position offset on the Z axis.
     */
    default int chests$zOff() {
        return 0;
    }
}
