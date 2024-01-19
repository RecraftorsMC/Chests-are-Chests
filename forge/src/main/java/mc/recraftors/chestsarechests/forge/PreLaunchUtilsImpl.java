package mc.recraftors.chestsarechests.forge;

import net.minecraftforge.fml.ModList;

import java.util.Optional;

public final class PreLaunchUtilsImpl {

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean modHasAuthor(String modId, String author) {
        Optional<?> opt = ModList.get().getModFileById(modId).getMods().get(0).getConfig().getConfigElement("authors");
        if (opt.isEmpty()) return false;
        String[] strings;
        Object o = opt.get();
        if (o instanceof String s) {
            strings = s.split(",");
        } else if (o instanceof String[] array) {
            strings = array;
        } else {
            return false;
        }
        for (String s : strings) {
            if (s.toLowerCase().replace(' ', '_').replace('-', '_').equals(author)) {
                return true;
            }
        }
        return false;
    }
}
