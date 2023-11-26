package mc.recraftors.chestsarechests;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final String BARREL_FALL_RULE_ID = "chests.barrelFall";
	public static final String CHEST_LID_FLING_RULE_ID = "chests.lidFling";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static GameRules.Key<GameRules.BooleanRule> barrelFall;
	private static GameRules.Key<GameRules.BooleanRule> lidFling;

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFall() {
		return barrelFall;
	}

	public static GameRules.Key<GameRules.BooleanRule> getLidFling() {
		return lidFling;
	}

	public static void setBarrelFall(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (barrelFall == null) {
			barrelFall = ruleKey;
		}
	}

	public static void setLidFling(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (lidFling == null) {
			lidFling = ruleKey;
		}
	}

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
