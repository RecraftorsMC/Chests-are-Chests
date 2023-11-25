package mc.recraftors.chestsarechests.forge;

import mc.recraftors.chestsarechests.ChestsAreChests;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(ChestsAreChests.MOD_ID)
public class ChestsAreChestsImpl {
    public ChestsAreChestsImpl() {
        ChestsAreChests.init();
    }

    public static boolean isModLoaded(String modId) {
        return FMLLoader.modLauncherModList().contains(modId);
    }
}