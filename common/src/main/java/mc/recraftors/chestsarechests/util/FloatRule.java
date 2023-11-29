package mc.recraftors.chestsarechests.util;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import mc.recraftors.chestsarechests.ChestsAreChests;
import mc.recraftors.chestsarechests.util.shadow.NamedRuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class FloatRule extends GameRules.Rule<FloatRule> {
    private float value;

    public FloatRule(GameRules.Type<FloatRule> rule, float initialValue) {
        super(rule);
        this.value = initialValue;
    }

    public static GameRules.Type<FloatRule> create(float initialValue, BiConsumer<MinecraftServer, FloatRule> changeCallback) {
        return new GameRules.Type<>(FloatArgumentType::floatArg, type -> new FloatRule(type, initialValue), changeCallback, (consumer, key, cType) -> ((GameRulesVisitor)consumer).chests$visitFloat(key, cType));
    }

    public static GameRules.Type<FloatRule> create(float initialValue) {
        return create(initialValue, ((server, floatRule) -> {}));
    }

    public float get() {
        return value;
    }

    public void set(float value, MinecraftServer server) {
        this.value = value;
        this.changed(server);
    }

    public boolean validate(String input) {
        try {
            this.value = Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
        this.value = FloatArgumentType.getFloat(context, name);
    }

    @Override
    protected void deserialize(String value) {
        this.value = FloatRule.parseFloat(value);
    }

    @Override
    public String serialize() {
        return Float.toString(this.value);
    }

    @Override
    public int getCommandResult() {
        return (int) this.value;
    }

    @Override
    protected FloatRule getThis() {
        return this;
    }

    @Override
    protected FloatRule copy() {
        return new FloatRule(this.type, this.value);
    }

    @Override
    public void setValue(FloatRule rule, @Nullable MinecraftServer server) {
        this.value = rule.value;
        this.changed(server);
    }

    private static float parseFloat(String input) {
        if (!input.isEmpty()) {
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                ChestsAreChests.LOGGER.warn("Failed to parse float {}", input);
            }
        }
        return 0;
    }

    public static class FloatRuleWidget extends NamedRuleWidget {
        private final TextFieldWidget valueWidget;
        public FloatRuleWidget(Text name, @Nullable List<OrderedText> description, String ruleName, FloatRule rule, EditGameRulesScreen screen) {
            super(description, name, screen);
            this.valueWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 10, 5, 42, 20, name.copy().append("\n").append(ruleName).append("\n"));
            this.valueWidget.setText(Float.toString(rule.get()));
            this.valueWidget.setChangedListener(val -> {
                if (rule.validate(val)) {
                    this.valueWidget.setEditableColor(0xE0E0E0);
                } else {
                    this.valueWidget.setEditableColor(0xFF0000);
                    screen.markInvalid(this);
                }
            });
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.drawName(context, y, x);
            this.valueWidget.setX(x + entryWidth - 44);
            this.valueWidget.setY(y);
            this.valueWidget.render(context, mouseX, mouseY, tickDelta);
        }
    }
}
