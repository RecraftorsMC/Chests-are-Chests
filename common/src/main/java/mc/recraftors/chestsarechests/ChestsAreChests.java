package mc.recraftors.chestsarechests;

import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.FloatRule;
import mc.recraftors.chestsarechests.util.GamerulesFloatProvider;
import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final String BARREL_FALL_RULE_ID = "chests.barrelFall";
	public static final String BARREL_FALL_SPECIAL_THROWABLE_RULE_ID = "chests.barrelFall.throwableSpecial";
	public static final String DISPENSER_OPEN_RULE_ID = "chests.dispenserOpen";
	public static final String DISPENSER_OPEN_DURATION_RULE_ID = "chests.dispenserOpen.duration";
	public static final String INSERT_OPEN_RULE_ID = "chests.insertOpen";
	public static final String CHEST_LID_FLING_RULE_ID = "chests.lidFling";
	public static final String CHEST_LID_HORIZONTAL_POWER_RULE_ID = "chests.lidFling.horizontalPower";
	public static final String CHESTS_LID_VERTICAL_POWER_RULE_ID = "chests.lidFling.verticalPower";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static GameRules.Key<GameRules.BooleanRule> barrelFall;
	private static GameRules.Key<GameRules.BooleanRule> barrelFallThrowableSpecial;
	private static GameRules.Key<GameRules.BooleanRule> dispenserOpen;
	private static GameRules.Key<GameRules.IntRule> dispenserOpenDuration;
	private static GameRules.Key<GameRules.BooleanRule> insertOpen;
	private static GameRules.Key<GameRules.BooleanRule> lidFling;
	private static GameRules.Key<FloatRule> lidHorizontalPower;
	private static GameRules.Key<FloatRule> lidVerticalPower;
	public static final TagKey<EntityType<?>> FLINGABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(MOD_ID, "flingable"));
	public static final TagKey<Item> SPECIAL_FALL = TagKey.of(RegistryKeys.ITEM, new Identifier(MOD_ID, "special_fall"));

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFall() {
		return barrelFall;
	}

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFallThrowableSpecial() {
		return barrelFallThrowableSpecial;
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

	public static void setBarrelFallThrowableSpecial(GameRules.Key<GameRules.BooleanRule> ruleKey) {
		if (barrelFallThrowableSpecial == null) {
			barrelFallThrowableSpecial = ruleKey;
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

	public static boolean automatedContainerOpening(ServerWorld world, BlockPos pos, BlockState state, Direction direction) {
		BlockPos target = pos.offset(direction, 1);
		BlockEntity entity = world.getBlockEntity(target);
		if (!(entity instanceof FallInContainer container)) return false;
		return (container.chests$isOpen() && container.chests$forceClose()) || container.chests$tryForceOpen(state);
	}

	public static void scheduleTick(ServerWorld world, BlockPos pos, int duration) {
		world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), duration);
	}

	public static void lidFlingEntity(Entity entity, Direction direction, float verticalMultiplier, float horizontalMultiplier) {
		int x = direction.getOffsetX();
		int z = direction.getOffsetZ();
		float horiz = ((GamerulesFloatProvider)entity.getWorld().getGameRules()).chests$getFloat(getLidHorizontalPower());
		float vert = ((GamerulesFloatProvider)entity.getWorld().getGameRules()).chests$getFloat(getLidVerticalPower());
		entity.addVelocity(horiz*x*horizontalMultiplier, vert*verticalMultiplier, -horiz*z*horizontalMultiplier);
	}

	public static void ejectAbove(Direction facing, BlockEntity entity) {
		eject(facing.getOpposite(), entity, 1, 1, 0, 1, 0);
	}

	public static void ejectBelow(Direction facing, BlockEntity entity) {
		eject(facing.getOpposite(), entity, -1, 1, 0, -1, 0);
	}

	public static void eject(Direction direction, BlockEntity entity, float verticalMultiplier, float horizontalMultiplier, int xOff, int yOff, int zOff) {
		World world = entity.getWorld();
		if (world == null) {
			return;
		}
		if (!world.getGameRules().getBoolean(getLidFling())) return;
		BlockPos pos = entity.getPos().offset(Direction.Axis.X, xOff).offset(Direction.Axis.Y, yOff).offset(Direction.Axis.Z, zOff);
		world.getOtherEntities(null, new Box(pos), e -> !e.isSpectator() && e.getType().isIn(FLINGABLE))
				.forEach(e -> lidFlingEntity(e, direction, verticalMultiplier, horizontalMultiplier));
	}

	public static int itemStackCustomHash(ItemStack stack) {
		Item item = stack.getItem();
		int i = ((RegistryIndexAccessor<Item>) Registries.ITEM).chests$getEntryIndex(item);
		// Should be unique enough
		return 197 * i + 19 * stack.getCount() + (stack.hasNbt() ? 7 : 1);
	}

	public static <T> boolean isInArray(T value, T[] array) {
		if (array == null) {
			return false;
		}
        for (T t : array) {
            if (Objects.equals(value, t)) return true;
        }
		return false;
	}
}
