package mc.recraftors.chestsarechests.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
