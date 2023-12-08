package mc.recraftors.chestsarechests.util.shadow;

import com.google.common.collect.Lists;
import mc.recraftors.chestsarechests.util.ClientProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class NamedRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
    private final List<OrderedText> name;
    protected final List<ClickableWidget> children;
    private final EditGameRulesScreen screen;

    @SuppressWarnings("resource")
    protected NamedRuleWidget(List<OrderedText> description, Text name, EditGameRulesScreen screen) {
        super(description);
        this.children = Lists.newArrayList();
        this.name = ((ClientProvider)screen).chests$getClient().textRenderer.wrapLines(name, 175);
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

    @SuppressWarnings("resource")
    protected void drawName(DrawContext context, int x, int y) {
        if (this.name.size() == 1) {
            context.drawText(((ClientProvider)screen).chests$getClient().textRenderer, this.name.get(0), y, x + 5, 16777215, false);
        } else if (this.name.size() >= 2) {
            context.drawText(((ClientProvider)screen).chests$getClient().textRenderer, this.name.get(0), y, x, 16777215, false);
            context.drawText(((ClientProvider)screen).chests$getClient().textRenderer, this.name.get(1), y, x + 10, 16777215, false);
        }
    }
}
