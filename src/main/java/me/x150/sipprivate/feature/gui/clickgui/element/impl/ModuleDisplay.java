package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.util.Utils;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;

public class ModuleDisplay extends Element {
    Module module;

    public ModuleDisplay(double x, double y, Module module) {
        super(x, y, 100, 15);
        this.module = module;
    }

    @Override public boolean clicked(double x, double y, int button) {
        if (inBounds(x, y)) {
            if (button == 0) module.setEnabled(!module.isEnabled()); // left click
            else return false;
            return true;
        }
        return false;
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY) {
        return false;
    }

    @Override public boolean released() {
        return false;
    }

    @Override public boolean keyPressed(int keycode) {
        return false;
    }

    @Override public void render(MatrixStack matrices) {
        Renderer.R2D.fill(matrices,Color.RED,x,y,x+width,y+height);
        FontRenderers.getNormal().drawCenteredString(matrices,module.getName(),x+width/2d,y+height/2d-FontRenderers.getNormal().getMarginHeight()/2d,0xFFFFFF);
        if (module.isEnabled()) {
            Renderer.R2D.fill(matrices, Utils.getCurrentRGB(), x, y, x+1, y+height);
        }
    }
}
