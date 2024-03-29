package mc.recraftors.chestsarechests.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().getAllMods().stream()
                .anyMatch(c -> c.getMetadata().getId().toLowerCase().replace('-', '_').equals(modId));
    }

    public static boolean modHasAuthor(String modId, String author) {
        return FabricLoader.getInstance().getAllMods().stream()
                .filter(c -> c.getMetadata().getId().toLowerCase().replace('-', '_').equals(modId))
                .anyMatch(mod -> mod.getMetadata().getAuthors().stream()
                        .anyMatch(p -> p.getName().toLowerCase()
                                .replace(' ', '_').replace('-', '_').equals(author)));
    }
}
