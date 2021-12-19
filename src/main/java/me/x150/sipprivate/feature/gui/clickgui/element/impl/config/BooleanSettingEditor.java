package me.x150.sipprivate.feature.gui.clickgui.element.impl.config;

import me.x150.sipprivate.config.BooleanSetting;
import me.x150.sipprivate.feature.gui.clickgui.Theme;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;

public class BooleanSettingEditor extends ConfigBase<BooleanSetting> {
    public BooleanSettingEditor(double x, double y, double width, BooleanSetting configValue) {
        super(x, y, width, FontRenderers.getNormal().getFontHeight()+2, configValue);
    }

    @Override public boolean clicked(double x, double y, int button) {
//        System.out.println(x+", "+y+", "+button);
        if (inBounds(x, y) && button == 0) {
//            System.out.println("clicked");
            configValue.setValue(!configValue.getValue());
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
    double rw = 14;
    double rh = 7;
    double rid = 5;
    @Override public void render(MatrixStack matrices) {
        Renderer.R2D.fill(matrices,configValue.getValue()? Theme.ACTIVE : Theme.INACTIVE,x,y+height/2d-rh/2d,x+rw,y+height/2d+rh/2d);
        double rix = configValue.getValue()?x+rw-rid-1:x+1;
        Renderer.R2D.fill(matrices,Theme.ACCENT,rix,y+height/2d-rh/2d+1,rix+rid,y+height/2d-rh/2d+1+rid);
        FontRenderers.getNormal().drawString(matrices,configValue.getName(), x+rw+2,y+height/2d-FontRenderers.getNormal().getMarginHeight()/2d,0xFFFFFF);
    }
}
