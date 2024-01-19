package mc.recraftors.chestsarechests.util;

/**
 * Utility interface to get the index of an element in a registry.
 * @param <T> The registry value type.
 */
public interface RegistryIndexAccessor <T> {
    default int chests$getEntryIndex(T value) {
        return -1;
    }
}
