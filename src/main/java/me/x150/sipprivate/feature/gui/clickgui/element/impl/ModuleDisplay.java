package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class ModuleDisplay extends Element {
    final Module module;
    final ConfigDisplay cd;
    boolean extended = false;
    double extendAnim = 0;
    long hoverStart = System.currentTimeMillis();
    boolean hoveredBefore = false;

    public ModuleDisplay(double x, double y, Module module) {
        super(x, y, 100, 15);
        this.module = module;
        this.cd = new ConfigDisplay(x, y, module.config);
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            if (button == 0) {
                module.setEnabled(!module.isEnabled()); // left click
            } else if (button == 1) {
                extended = !extended;
            } else {
                return false;
            }
            return true;
        } else {
            return extended && cd.clicked(x, y, button);
        }
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return extended && cd.dragged(x, y, deltaX, deltaY, button);
    }

    @Override
    public boolean released() {
        return extended && cd.released();
    }

    double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    }

    @Override
    public double getHeight() {
        return super.getHeight() + cd.getHeight() * easeInOutCubic(extendAnim);
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        return extended && cd.keyPressed(keycode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {

        Theme theme = ClickGUI.theme;
        boolean hovered = inBounds(mouseX, mouseY);
        if (!hoveredBefore && hovered) {
            hoverStart = System.currentTimeMillis();
        }
        if (hoverStart + 500 < System.currentTimeMillis() && hovered) {
            ClickGUI.instance().renderDescription(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY() + 10, module.getDescription());
        }
        hoveredBefore = hovered;
        Renderer.R2D.renderQuad(matrices, hovered ? theme.getModule().darker() : theme.getModule(), x, y, x + width, y + height);
        FontRenderers.getNormal().drawCenteredString(matrices, module.getName(), x + width / 2d, y + height / 2d - FontRenderers.getNormal().getMarginHeight() / 2d, 0xFFFFFF);
        if (module.isEnabled()) {
//            Renderer.R2D.renderQuad(matrices, theme.getAccent(), x, y, x + 1, y + height);
            double wid = 1.5;
            Renderer.R2D.renderRoundedQuad(matrices, theme.getAccent(), x + 1, y + 1, x + 1 + wid, y + height - 1, wid / 2d, 6);
        }
        cd.setX(this.x);
        cd.setY(this.y + height);

        Renderer.R2D.beginScissor(matrices, x, y, x + width, y + getHeight());
        if (extendAnim > 0) {
            cd.render(matrices, mouseX, mouseY, getHeight() - super.getHeight());
        }
        Renderer.R2D.endScissor();
    }

    @Override
    public void tickAnim() {
        double a = 0.04;
        if (!extended) {
            a *= -1;
        }
        extendAnim += a;
        extendAnim = MathHelper.clamp(extendAnim, 0, 1);
        cd.tickAnim();
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return extended && cd.charTyped(c, mods);
    }
}
