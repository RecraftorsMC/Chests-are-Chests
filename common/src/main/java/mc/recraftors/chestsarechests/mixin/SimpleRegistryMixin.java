package mc.recraftors.chestsarechests.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin <T> implements RegistryIndexAccessor<T>, MutableRegistry<T> {
    @Shadow @Final private Object2IntMap<T> entryToRawId;

    @Override
    public int chests$getEntryIndex(T value) {
        return this.entryToRawId.getOrDefault(value, -1);
    }
}
