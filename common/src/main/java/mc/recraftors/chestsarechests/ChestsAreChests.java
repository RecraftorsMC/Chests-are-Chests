package mc.recraftors.chestsarechests;

import dev.architectury.injectables.annotations.ExpectPlatform;
import mc.recraftors.chestsarechests.util.FloatRule;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final String BARREL_FALL_RULE_ID = "chests.barrelFall";
	public static final String DISPENSER_OPEN_RULE_ID = "chests.dispenserOpen";
	public static final String INSERT_OPEN_ID = "chests.insertOpen";
	public static final String CHEST_LID_FLING_RULE_ID = "chests.lidFling";
	public static final String CHEST_LID_HORIZONTAL_POWER_RULE_ID = "chests.lidFling.horizontalPower";
	public static final String CHESTS_LID_VERTICAL_POWER_RULE_ID = "chests.lidFling.verticalPower";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static GameRules.Key<GameRules.BooleanRule> barrelFall;
	private static GameRules.Key<GameRules.BooleanRule> dispenserOpen;
	private static GameRules.Key<GameRules.BooleanRule> insertOpen;
	private static GameRules.Key<GameRules.BooleanRule> lidFling;
	private static GameRules.Key<FloatRule> lidHorizontalPower;
	private static GameRules.Key<FloatRule> lidVerticalPower;

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFall() {
		return barrelFall;
	}

	public static GameRules.Key<GameRules.BooleanRule> getDispenserOpen() {
		return dispenserOpen;
	}

	public static GameRules.Key<GameRules.BooleanRule> getLidFling() {
		return lidFling;
	}

	public static GameRules.Key<FloatRule> getLidHorizontalPower() {
		return lidHorizontalPower;
	}

	public static GameRules.Key<FloatRule> getLidVerticalPower() {
		return lidVerticalPower;
	}

	public static GameRules.Key<GameRules.BooleanRule> getInsertOpen() {
		return insertOpen;
	}

	public static void setBarrelFall(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (barrelFall == null) {
			barrelFall = ruleKey;
		}
	}

	public static void setDispenserOpen(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (dispenserOpen == null) {
			dispenserOpen = ruleKey;
		}
	}

	public static void setInsertOpen(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (insertOpen == null) {
			insertOpen = ruleKey;
		}
	}

	public static void setLidFling(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (lidFling == null) {
			lidFling = ruleKey;
		}
	}

	public static void setLidHorizontalPower(GameRules.Key<FloatRule> ruleKey) {
		if (lidHorizontalPower == null) {
			lidHorizontalPower = ruleKey;
		}
	}

	public static void setLidVerticalPower(GameRules.Key<FloatRule> ruleKey) {
		if (lidVerticalPower == null) {
			lidVerticalPower = ruleKey;
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
