package mc.recraftors.chestsarechests.fabric;

import mc.recraftors.chestsarechests.ChestsAreChests;
import net.fabricmc.api.ModInitializer;

public class ChestsAreChestsImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        ChestsAreChests.init();
    }
}