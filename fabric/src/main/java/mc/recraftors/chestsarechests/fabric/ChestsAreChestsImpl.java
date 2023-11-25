package mc.recraftors.chestsarechests.fabric;

import mc.recraftors.chestsarechests.ChestsAreChests;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ChestsAreChestsImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        ChestsAreChests.init();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}