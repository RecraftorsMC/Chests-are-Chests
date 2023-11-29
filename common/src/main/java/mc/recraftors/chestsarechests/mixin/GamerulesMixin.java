package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.FloatRule;
import mc.recraftors.chestsarechests.util.GamerulesFloatProvider;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public abstract class GamerulesMixin implements GamerulesFloatProvider {
    @Shadow
    public static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return null;
    }

    @Shadow public abstract <T extends GameRules.Rule<T>> T get(GameRules.Key<T> key);

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void tailInjector(CallbackInfo ci) {
        ChestsAreChests.setBarrelFall(register(ChestsAreChests.BARREL_FALL_RULE_ID, GameRules.Category.DROPS, GameRules.BooleanRule.create(false)));
        ChestsAreChests.setLidFling(register(ChestsAreChests.CHEST_LID_FLING_RULE_ID, GameRules.Category.MISC, GameRules.BooleanRule.create(false)));
        ChestsAreChests.setLidHorizontalPower(register(ChestsAreChests.CHEST_LID_HORIZONTAL_POWER_RULE_ID, GameRules.Category.MISC, FloatRule.create(.25f)));
        ChestsAreChests.setLidVerticalPower(register(ChestsAreChests.CHESTS_LID_VERTICAL_POWER_RULE_ID, GameRules.Category.MISC, FloatRule.create(.6f)));
        ChestsAreChests.setInsertOpen(register(ChestsAreChests.INSERT_OPEN_ID, GameRules.Category.DROPS, GameRules.BooleanRule.create(true)));
        ChestsAreChests.setDispenserOpen(register(ChestsAreChests.DISPENSER_OPEN_RULE_ID, GameRules.Category.MISC, GameRules.BooleanRule.create(true)));
    }

    @Unique
    @Override
    public float chests$getFloat(GameRules.Key<FloatRule> key) {
        return this.get(key).get();
    }
}
