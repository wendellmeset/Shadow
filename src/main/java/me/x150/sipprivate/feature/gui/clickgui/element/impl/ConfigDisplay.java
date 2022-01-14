package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.config.*;
import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.config.*;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigDisplay extends Element {
    final List<ConfigBase<?>> bases = new ArrayList<>();
    final ModuleConfig mc;
    final double padding = 4;
    final double paddingLeft = 3;
    long hoverStart = System.currentTimeMillis();
    boolean hoveredBefore = false;

    public ConfigDisplay(double x, double y, ModuleConfig mc) {
        super(x, y, 100, 0);
        this.mc = mc;
        for (SettingBase<?> setting : mc.getSettings()) {
            if (setting instanceof BooleanSetting set) {
                BooleanSettingEditor bse = new BooleanSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(bse);
            } else if (setting instanceof DoubleSetting set) {
                DoubleSettingEditor dse = new DoubleSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(dse);
            } else if (setting instanceof EnumSetting<?> set) {
                EnumSettingEditor ese = new EnumSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(ese);
            } else if (setting instanceof StringSetting set) {
                StringSettingEditor sse = new StringSettingEditor(0, 0, width - padding - paddingLeft, set);
                bases.add(sse);
            }
        }
        this.height = bases.stream().map(Element::getHeight).reduce(Double::sum).orElse(0d);
    }

    public List<ConfigBase<?>> getBases() {
        return bases.stream().filter(configBase -> configBase.getConfigValue().shouldShow()).collect(Collectors.toList());
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.getConfigValue().shouldShow() && basis.clicked(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.getConfigValue().shouldShow() && basis.dragged(x, y, deltaX, deltaY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean released() {
        for (ConfigBase<?> basis : bases) {
            if (basis.getConfigValue().shouldShow()) {
                basis.released();
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.getConfigValue().shouldShow() && basis.keyPressed(keycode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getHeight() {
        this.height = 4 + getBases().stream().map(Element::getHeight).reduce(Double::sum).orElse(0d);
        return super.getHeight();
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        double yOffset = 2;
        Theme theme = ClickGUI.theme;
        double height = getHeight();
        Renderer.R2D.renderQuad(matrices, theme.getConfig(), x, this.y, x + width, this.y + height);
        Renderer.R2D.renderQuad(matrices, theme.getAccent(), x, this.y, x + 1, this.y + height);
        boolean hovered = inBounds(mouseX, mouseY);
        if (!hoveredBefore && hovered) {
            hoverStart = System.currentTimeMillis();
        }
        hoveredBefore = hovered;
        String renderingDesc = null;
        for (ConfigBase<?> basis : getBases()) {
            basis.setX(x + padding);
            basis.setY(this.y + yOffset);
            if (!basis.getConfigValue().shouldShow()) {
                continue;
            }
            if (y + scrollBeingUsed > basis.getY()) {
                basis.render(matrices, mouseX, mouseY, scrollBeingUsed);
                if (basis.inBounds(mouseX, mouseY) && renderingDesc == null) {
                    renderingDesc = basis.getConfigValue().description;
                }
            }
            yOffset += basis.getHeight();
        }
        if (hoverStart + 500 < System.currentTimeMillis() && hovered && renderingDesc != null) {
            ClickGUI.instance().renderDescription(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY() + 10, renderingDesc);
        }
    }

    @Override
    public void tickAnim() {
        for (ConfigBase<?> basis : bases) {
            basis.tickAnim();
        }

    }

    @Override
    public boolean charTyped(char c, int mods) {
        for (ConfigBase<?> basis : getBases()) {
            if (basis.getConfigValue().shouldShow() && basis.charTyped(c, mods)) {
                return true;
            }
        }
        return false;
    }
}
