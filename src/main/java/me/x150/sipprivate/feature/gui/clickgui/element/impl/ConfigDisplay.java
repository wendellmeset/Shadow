package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.config.BooleanSetting;
import me.x150.sipprivate.config.ModuleConfig;
import me.x150.sipprivate.config.SettingBase;
import me.x150.sipprivate.feature.gui.clickgui.Theme;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.config.BooleanSettingEditor;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.config.ConfigBase;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigDisplay extends Element {
    List<ConfigBase<?>> bases = new ArrayList<>();
    ModuleConfig mc;
    public ConfigDisplay(double x, double y, ModuleConfig mc) {
        super(x, y, 100, mc.getSettings().size()* FontRenderers.getNormal().getMarginHeight());
        this.mc = mc;
        for (SettingBase<?> setting : mc.getSettings()) {
            if (setting instanceof BooleanSetting set) {
                BooleanSettingEditor bse = new BooleanSettingEditor(0,0,100,set);
                bases.add(bse);
            }
        }
    }

    @Override public boolean clicked(double x, double y, int button) {
        for (ConfigBase<?> basis : bases) {
            if (basis.clicked(x,y,button)) return true;
        }
        return false;
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY) {
        for (ConfigBase<?> basis : bases) {
            if (basis.dragged(x, y, deltaX, deltaY)) return true;
        }
        return false;
    }

    @Override public boolean released() {
        for (ConfigBase<?> basis : bases) {
            basis.released();
        }
        return false;
    }

    @Override public boolean keyPressed(int keycode) {
        for (ConfigBase<?> basis : bases) {
            if (basis.keyPressed(keycode)) return true;
        }
        return false;
    }

    @Override public void render(MatrixStack matrices) {
        double yOffset = 0;
        Renderer.R2D.fill(matrices, Theme.ACCENT, x, this.y, x+1, this.y+height);
        Renderer.R2D.fill(matrices,Theme.CONFIG,x,this.y,x+width,this.y+height);
//        for (SettingBase<?> setting : mc.getSettings()) {
//            FontRenderers.getNormal().drawString(matrices,setting.getName(),x,this.y+yOffset,0xFFFFFF);
//            yOffset += FontRenderers.getNormal().getMarginHeight();
//        }
        for (ConfigBase<?> basis : bases) {
            basis.x = x+2;
            basis.y = this.y+yOffset;
            basis.render(matrices);
            yOffset += basis.getHeight();
        }
        this.height = yOffset;
    }
}
