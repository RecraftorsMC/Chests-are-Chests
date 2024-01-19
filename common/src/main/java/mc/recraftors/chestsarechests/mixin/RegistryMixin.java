package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.collection.IndexedIterable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
public abstract class RegistryMixin <T> implements RegistryIndexAccessor<T>, IndexedIterable<T> {
    @Override
    public int chests$getEntryIndex(T value) {
        return -1;
    }
}
