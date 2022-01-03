package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class CategoryDisplay extends Element {
    static ClientFontRenderer cfr = FontRenderers.getCustomNormal(20);
    boolean             selected = false;
    List<ModuleDisplay> md       = new ArrayList<>();
    ModuleType          mt;

    public CategoryDisplay(double x, double y, ModuleType mt) {
        super(x, y, 100, 500);
        this.mt = mt;
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == mt) {
                ModuleDisplay md1 = new ModuleDisplay(0, 0, module);
                md.add(md1);
            }
        }
    }

    @Override public boolean clicked(double x, double y, int button) {
        if (x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + headerHeight()) {
            if (button == 0) {
                selected = true;
                return true;
            }
        } else {
            for (ModuleDisplay moduleDisplay : md) {
                if (moduleDisplay.clicked(x, y, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        if (selected) {
            this.x += deltaX;
            this.y += deltaY;
            return true;
        } else {
            for (ModuleDisplay moduleDisplay : md) {
                if (moduleDisplay.dragged(x, y, deltaX, deltaY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override public boolean released() {
        selected = false;
        for (ModuleDisplay moduleDisplay : md) {
            moduleDisplay.released();
        }
        return false;
    }

    @Override public boolean keyPressed(int keycode) {
        for (ModuleDisplay moduleDisplay : md) {
            if (moduleDisplay.keyPressed(keycode)) {
                return true;
            }
        }
        return false;
    }

    float headerHeight() {
        float padding = 3;
        return padding + cfr.getFontHeight() + padding;
    }

    @Override public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Theme theme = ClickGUI.theme;
        //        Renderer.R2D.fill(matrices, theme.getHeader(), x, y, x + width, y + headerHeight());
        double r = ModuleRegistry.getByClass(me.x150.sipprivate.feature.module.impl.render.ClickGUI.class).radius.getValue();
        double preCalcHeight = md.stream().map(ModuleDisplay::getHeight).reduce(Double::sum).orElse(0d) + headerHeight();
        Renderer.R2D.renderRoundedQuad(matrices, theme.getHeader(), x, y, x + width, y + preCalcHeight + r, r, 15);
        cfr.drawCenteredString(matrices, mt.getName(), x + width / 2d, y + headerHeight() / 2d - cfr.getFontHeight() / 2d, 0xFFFFFF);
        double y = headerHeight();
        for (ModuleDisplay moduleDisplay : md) {
            moduleDisplay.setX(this.x);
            moduleDisplay.setY(this.y + y);
            moduleDisplay.render(matrices, mouseX, mouseY, scrollBeingUsed);
            y += moduleDisplay.getHeight();
        }
        this.height = y;
    }

    @Override public void tickAnim() {
        for (ModuleDisplay moduleDisplay : md) {
            moduleDisplay.tickAnim();
        }
    }
}
