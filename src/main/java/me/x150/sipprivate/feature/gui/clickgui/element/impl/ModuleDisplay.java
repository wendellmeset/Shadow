package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.gui.clickgui.Theme;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.util.Utils;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleDisplay extends Element {
    Module module;
    ConfigDisplay cd;

    public ModuleDisplay(double x, double y, Module module) {
        super(x, y, 100, 15);
        this.module = module;
        this.cd = new ConfigDisplay(x,y,module.config);
    }

    @Override public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            if (button == 0) {
                module.setEnabled(!module.isEnabled()); // left click
            } else if (button == 1) {
                extended = !extended;
            } else {
                return false;
            }
            return true;
        } else
            return extended && cd.clicked(x, y, button);
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY) {
        return extended && cd.dragged(x, y, deltaX, deltaY);
    }

    @Override public boolean released() {
        return extended && cd.released();
    }

    @Override public double getHeight() {
        return super.getHeight()+(extended?cd.getHeight():0);
    }

    @Override public boolean keyPressed(int keycode) {
        return extended && cd.keyPressed(keycode);
    }
    boolean extended = false;
    @Override public void render(MatrixStack matrices) {
        boolean hovered = inBounds(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY());
        Renderer.R2D.fill(matrices, hovered?Theme.MODULE.darker():Theme.MODULE, x, y, x + width, y + height);
        FontRenderers.getNormal().drawCenteredString(matrices, module.getName(), x + width / 2d, y + height / 2d - FontRenderers.getNormal().getMarginHeight() / 2d, 0xFFFFFF);
        if (module.isEnabled()) {
            Renderer.R2D.fill(matrices, Theme.ACCENT, x, y, x + 1, y + height);
        }
        cd.x = this.x;
        cd.y = this.y+height;
        if (extended) cd.render(matrices);
    }
}
