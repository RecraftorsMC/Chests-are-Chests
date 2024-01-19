package mc.recraftors.chestsarechests.forge;

import net.minecraftforge.fml.ModList;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
