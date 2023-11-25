package mc.recraftors.chestsarechests;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void init() {
	}

	@Contract
	@ExpectPlatform
	public static boolean isModLoaded(String modId) {
		throw new UnsupportedOperationException();
	}

	public static boolean canMergeItems(ItemStack first, ItemStack second) {
		if (!first.isOf(second.getItem())) {
			return false;
		}
		if (first.getDamage() != second.getDamage()) {
			return false;
		}
		if (first.getCount() > first.getMaxCount()) {
			return false;
		}
		return ItemStack.areNbtEqual(first, second);
	}
}
