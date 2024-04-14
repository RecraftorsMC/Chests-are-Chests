package mc.recraftors.chestsarechests;

import mc.recraftors.chestsarechests.util.FallInContainer;
import mc.recraftors.chestsarechests.util.LidFlingHelper;
import mc.recraftors.chestsarechests.util.RegistryIndexAccessor;
import mc.recraftors.unruled_api.rules.FloatRule;
import mc.recraftors.unruled_api.UnruledApi;
import mc.recraftors.unruled_api.utils.IGameRulesProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ChestsAreChests {
	public static final String MOD_ID = "chests_are_chests";
	public static final String BARREL_FALL_RULE_ID = "chests.barrelFall";
	public static final String BARREL_FALL_SPECIAL_THROWABLE_RULE_ID = "chests.barrelFall.throwableSpecial";
	public static final String BARREL_FALL_SPREAD_RADIUS_RULE_ID = "chests.barrelFall.spreadRadius";
	public static final String DISPENSER_OPEN_RULE_ID = "chests.dispenserOpen";
	public static final String DISPENSER_OPEN_DURATION_RULE_ID = "chests.dispenserOpen.duration";
	public static final String INSERT_OPEN_RULE_ID = "chests.insertOpen";
	public static final String CHEST_LID_FLING_RULE_ID = "chests.lidFling";
	public static final String CHEST_LID_HORIZONTAL_POWER_RULE_ID = "chests.lidFling.horizontalPower";
	public static final String CHESTS_LID_VERTICAL_POWER_RULE_ID = "chests.lidFling.verticalPower";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static GameRules.Key<GameRules.BooleanRule> barrelFall;
	private static GameRules.Key<GameRules.BooleanRule> barrelFallThrowableSpecial;
	private static GameRules.Key<FloatRule> barrelFallRandomSpreadRadius;
	private static GameRules.Key<GameRules.BooleanRule> dispenserOpen;
	private static GameRules.Key<GameRules.IntRule> dispenserOpenDuration;
	private static GameRules.Key<GameRules.BooleanRule> insertOpen;
	private static GameRules.Key<GameRules.BooleanRule> lidFling;
	private static GameRules.Key<FloatRule> lidHorizontalPower;
	private static GameRules.Key<FloatRule> lidVerticalPower;
	public static final TagKey<EntityType<?>> FLINGABLE = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(MOD_ID, "flingable"));
	public static final TagKey<EntityType<?>> DOES_NOT_FALL_HATCH = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(MOD_ID, "does_not_fall_hatch"));
	public static final TagKey<Item> SPECIAL_FALL = TagKey.of(Registry.ITEM_KEY, new Identifier(MOD_ID, "special_fall"));

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFall() {
		return barrelFall;
	}

	public static GameRules.Key<GameRules.BooleanRule> getBarrelFallThrowableSpecial() {
		return barrelFallThrowableSpecial;
	}

	public static GameRules.Key<FloatRule> getBarrelFallRandomSpreadRadius() {
		return barrelFallRandomSpreadRadius;
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

	public static void init() {
		LOGGER.debug("{} loaded", MOD_ID);
		initializeGamerules();
		LOGGER.info("{} initialized", MOD_ID);
	}

	private static void initializeGamerules() {
		if (barrelFall == null) {
			barrelFall = UnruledApi.registerBoolean(BARREL_FALL_RULE_ID, GameRules.Category.DROPS, false);
		}
		if (barrelFallThrowableSpecial == null) {
			barrelFallThrowableSpecial = UnruledApi.registerBoolean(BARREL_FALL_SPECIAL_THROWABLE_RULE_ID, GameRules.Category.DROPS, true);
		}
		if (barrelFallRandomSpreadRadius == null) {
			barrelFallRandomSpreadRadius = UnruledApi.registerFloat(BARREL_FALL_SPREAD_RADIUS_RULE_ID,
					GameRules.Category.DROPS, 0f, ((minecraftServer, floatRule) -> {
						// min = 0, max = 0.5
						if (floatRule.get() < 0) floatRule.set(0, minecraftServer);
						else if (floatRule.get() > .5f) floatRule.set(.5f, minecraftServer);
					}));
		}
		if (lidFling == null) {
			lidFling = UnruledApi.registerBoolean(CHEST_LID_FLING_RULE_ID, GameRules.Category.MISC, false);
		}
		if (lidHorizontalPower == null) {
			lidHorizontalPower = UnruledApi.registerFloat(CHEST_LID_HORIZONTAL_POWER_RULE_ID, GameRules.Category.MISC, .25f);
		}
		if (lidVerticalPower == null) {
			lidVerticalPower = UnruledApi.registerFloat(CHESTS_LID_VERTICAL_POWER_RULE_ID, GameRules.Category.MISC, .6f);
		}
		if (insertOpen == null) {
			insertOpen = UnruledApi.registerBoolean(INSERT_OPEN_RULE_ID, GameRules.Category.DROPS, true);
		}
		if (dispenserOpen == null) {
			dispenserOpen = UnruledApi.registerBoolean(DISPENSER_OPEN_RULE_ID, GameRules.Category.MISC, true);
		}
		if (dispenserOpenDuration == null) {
			dispenserOpenDuration = UnruledApi.registerInt(DISPENSER_OPEN_DURATION_RULE_ID, GameRules.Category.MISC,
					10, (server, rule) -> {if (rule.get() < 1) rule.set(1, server);});
		}
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

	public static boolean automatedContainerOpening(ServerWorld world, BlockPos pos, BlockState state, Direction direction) {
		BlockPos target = pos.offset(direction, 1);
		BlockEntity entity = world.getBlockEntity(target);
		if (!(entity instanceof FallInContainer container)) return false;
		return (container.chests$isOpen() && container.chests$forceClose()) || container.chests$tryForceOpen(state);
	}

	public static void scheduleTick(ServerWorld world, BlockPos pos, int duration) {
		world.createAndScheduleBlockTick(pos, world.getBlockState(pos).getBlock(), duration);
	}

	public static void lidFlingEntity(Entity entity, Direction direction, float verticalMultiplier, float horizontalMultiplier) {
		int x = direction.getOffsetX();
		int z = direction.getOffsetZ();
		float horiz = ((IGameRulesProvider)entity.getWorld().getGameRules()).unruled_getFloat(getLidHorizontalPower());
		float vert = ((IGameRulesProvider)entity.getWorld().getGameRules()).unruled_getFloat(getLidVerticalPower());
		entity.addVelocity(horiz*x*horizontalMultiplier, vert*verticalMultiplier, -horiz*z*horizontalMultiplier);
	}

	public static void ejectAbove(Direction facing, BlockEntity entity) {
		eject(facing.getOpposite(), entity, 1, 1, 0, 1, 0);
	}

	public static void ejectBelow(Direction facing, BlockEntity entity) {
		eject(facing.getOpposite(), entity, -1, 1, 0, -1, 0);
	}

	public static void eject(Direction direction, BlockEntity entity, LidFlingHelper helper) {
		eject(direction, entity, helper.chests$verticalFactor(), helper.chests$horizontalFactor(), helper.chests$xOff(), helper.chests$yOff(), helper.chests$zOff());
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

	@SuppressWarnings("unchecked")
	public static int itemStackCustomHash(ItemStack stack) {
		Item item = stack.getItem();
		int i = ((RegistryIndexAccessor<Item>) Registry.ITEM).chests$getEntryIndex(item);
		// Should be unique enough
		return 197 * i + 19 * stack.getCount() + (stack.hasNbt() ? 7 : 1);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
