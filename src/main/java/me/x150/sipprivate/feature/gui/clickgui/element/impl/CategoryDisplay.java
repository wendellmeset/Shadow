package me.x150.sipprivate.feature.gui.clickgui.element.impl;

import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.system.CallbackI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CategoryDisplay extends Element {
    boolean selected = false;
    List<ModuleDisplay> md = new ArrayList<>();
    ModuleType mt;
    public CategoryDisplay(double x, double y, ModuleType mt) {
        super(x, y, 100, 500);
        this.mt = mt;
        for (Module module : ModuleRegistry.getModules()) {
            if (module.getModuleType() == mt) {
                ModuleDisplay md1 = new ModuleDisplay(0,0,module);
                md.add(md1);
            }
        }
    }
    static ClientFontRenderer cfr = FontRenderers.getCustomNormal(20);
    @Override public boolean clicked(double x, double y, int button) {
        if (x >= this.x && x <= this.x+this.width && y >= this.y && y <= this.y+headerHeight()) {
            selected = true;
            return true;
        } else {
            for (ModuleDisplay moduleDisplay : md) {
                if (moduleDisplay.clicked(x, y, button)) return true;
            }
        }
        return false;
    }

    @Override public boolean dragged(double x, double y, double deltaX, double deltaY) {
        if (selected) {
            this.x += deltaX;
            this.y += deltaY;
            return true;
        } else {
            for (ModuleDisplay moduleDisplay : md) {
                if (moduleDisplay.dragged(x, y, deltaX, deltaY)) return true;
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
            if (moduleDisplay.keyPressed(keycode)) return true;
        }
        return false;
    }

    float headerHeight() {
        float padding = 3;
        return padding + cfr.getFontHeight() + padding;
    }

    @Override public void render(MatrixStack matrices) {
        Renderer.R2D.fill(matrices,Color.BLUE,x,y,x+width,y+headerHeight());
        cfr.drawCenteredString(matrices,mt.getName(),x+width/2d,y+headerHeight()/2d-cfr.getFontHeight()/2d,0xFFFFFF);
        double y = headerHeight();
        for (ModuleDisplay moduleDisplay : md) {
            moduleDisplay.x = this.x;
            moduleDisplay.y = this.y+y;
            moduleDisplay.render(matrices);
            y += moduleDisplay.height;
        }
        this.height = y;
    }
}
