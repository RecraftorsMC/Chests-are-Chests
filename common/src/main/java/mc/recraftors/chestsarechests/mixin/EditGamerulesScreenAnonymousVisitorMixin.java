package mc.recraftors.chestsarechests.mixin;

import mc.recraftors.chestsarechests.util.FloatRule;
import mc.recraftors.chestsarechests.util.GameRulesVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget$1")
public abstract class EditGamerulesScreenAnonymousVisitorMixin implements GameRulesVisitor {

    @Shadow
    public <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.Key<T> key, EditGameRulesScreen.RuleWidgetFactory<T> widgetFactory) {}

    @Override
    public void chests$visitFloat(GameRules.Key<FloatRule> key, GameRules.Type<FloatRule> type) {
        this.createRuleWidget(key, ((name, description, ruleName, rule) -> new FloatRule.FloatRuleWidget(name, description, ruleName, rule, (EditGameRulesScreen) MinecraftClient.getInstance().currentScreen)));
        GameRulesVisitor.super.chests$visitFloat(key, type);
    }
}
