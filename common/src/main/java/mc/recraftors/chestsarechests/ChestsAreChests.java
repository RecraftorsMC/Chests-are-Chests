package mc.recraftors.chestsarechests;

import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.FloatRule;
import mc.recraftors.chestsarechests.util.GamerulesFloatProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final String BARREL_FALL_RULE_ID = "chests.barrelFall";
	public static final String DISPENSER_OPEN_RULE_ID = "chests.dispenserOpen";
	public static final String DISPENSER_OPEN_DURATION_RULE_ID = "chests.dispenserOpen.duration";
	public static final String INSERT_OPEN_ID = "chests.insertOpen";
	public static final String CHEST_LID_FLING_RULE_ID = "chests.lidFling";
	public static final String CHEST_LID_HORIZONTAL_POWER_RULE_ID = "chests.lidFling.horizontalPower";
	public static final String CHESTS_LID_VERTICAL_POWER_RULE_ID = "chests.lidFling.verticalPower";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static GameRules.Key<GameRules.BooleanRule> barrelFall;
	private static GameRules.Key<GameRules.BooleanRule> dispenserOpen;
	private static GameRules.Key<GameRules.IntRule> dispenserOpenDuration;
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

	public static GameRules.Key<GameRules.IntRule> getDispenserOpenDuration() {
		return dispenserOpenDuration;
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

	public static void setDispenserOpenDuration(GameRules.Key<GameRules.IntRule> ruleKey) {
		if (dispenserOpenDuration == null) {
			dispenserOpenDuration = ruleKey;
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
		LOGGER.debug("{} loaded", MOD_ID);
		LOGGER.debug("{} initialized", MOD_ID);
	}

	public static boolean canMergeItems(ItemStack first, ItemStack second) {
		return first.getCount() <= first.getMaxCount() && ItemStack.canCombine(first, second);
	}

	public static void dropAllDown(Inventory inventory, BlockEntity entity) {
		int s = inventory.size();
		for (int i = 0; i < s; i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;
			ItemEntity item = new ItemEntity(entity.getWorld(), entity.getPos().getX()+.5, entity.getPos().getY()-.5, entity.getPos().getZ()+.5, stack.copy());
			item.setVelocity(0, -.05, 0);
			entity.getWorld().spawnEntity(item);
			inventory.removeStack(i);
			inventory.markDirty();
		}
	}

	public static void lidFlingItem(ItemEntity entity, Direction direction) {
		int x = direction.getOffsetX();
		int z = direction.getOffsetZ();
		float horiz = ((GamerulesFloatProvider)entity.getWorld().getGameRules()).chests$getFloat(getLidHorizontalPower());
		float vert = ((GamerulesFloatProvider)entity.getWorld().getGameRules()).chests$getFloat(getLidVerticalPower());
		entity.addVelocity(-horiz*x, vert, -horiz*z);
	}

	public static boolean automatedContainerOpening(ServerWorld world, BlockPos pos, BlockState state, Direction direction) {
		BlockPos target = pos.offset(direction, 1);
		BlockEntity entity = world.getBlockEntity(target);
		if (!(entity instanceof FallInContainer container)) return false;
		return (container.chests$isOpen() && container.chests$forceClose()) || container.chests$tryForceOpen(state);
	}

	public static void scheduleTick(ServerWorld world, BlockPos pos, int duration) {
		world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), duration);
	}
}
