package mc.recraftors.chestsarechests.forge;

import net.minecraftforge.fml.loading.FMLLoader;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return FMLLoader.modLauncherModList().stream().anyMatch(map -> map.containsKey(modId));
    }
}
