package mc.recraftors.chestsarechests.util.shadow;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class NamedRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
    private final List<OrderedText> name;
    private final List<ClickableWidget> children;
    private final EditGameRulesScreen screen;

    protected NamedRuleWidget(List<OrderedText> description, Text name, EditGameRulesScreen screen) {
        super(description);
        this.children = Lists.newArrayList();
        this.name = screen.client.textRenderer.wrapLines(name, 175);
        this.screen = screen;
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return children;
    }

    protected void drawName(MatrixStack matrices, int x, int y) {
        if (this.name.size() == 1) {
            screen.client.textRenderer.draw(matrices, this.name.get(0), y, (x + 5), 0xFFFFFF);
        } else if (this.name.size() >= 2) {
            screen.client.textRenderer.draw(matrices, this.name.get(0), y, x, 0xFFFFFF);
            screen.client.textRenderer.draw(matrices, this.name.get(1), y, (x + 10), 0xFFFFFF);
        }
    }
}
