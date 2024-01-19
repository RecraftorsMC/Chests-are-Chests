package mc.recraftors.chestsarechests.mixin;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin <T> extends MutableRegistry<T> implements RegistryIndexAccessor<T> {
    @Shadow @Final private Object2IntMap<T> entryToRawId;

    SimpleRegistryMixin(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    @Override
    public int chests$getEntryIndex(T value) {
        return this.entryToRawId.getOrDefault(value, -1);
    }
}
