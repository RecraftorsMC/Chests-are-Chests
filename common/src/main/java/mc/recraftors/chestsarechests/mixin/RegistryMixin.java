package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import net.minecraft.registry.Registry;
import net.minecraft.util.collection.IndexedIterable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
public interface RegistryMixin <T> extends RegistryIndexAccessor<T>, IndexedIterable<T> {
    @Override
    default int chests$getEntryIndex(T value) {
        return -1;
    }
}
