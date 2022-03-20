/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.panels.elements;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.Renderer;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.lang.reflect.Field;

public class PanelButton extends Element {
    static double h = FontRenderers.getRenderer().getFontHeight()+2;
    boolean selecting = false;
    boolean cancelNextCharTyped = false;
    Runnable code;
    String title;


    public PanelButton(double x, double y, double width, String title, Runnable code) {
        super(x, y, width, h);
        this.code = code;
        this.title = title;
    }

    @Override
    public double getHeight() {
        return h;
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if (inBounds(x,y)) {
            code.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        return false;
    }

    @Override
    public boolean released() {
        return false;
    }
//    long lastUpdate = System.currentTimeMillis();
    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        Renderer.R2D.renderRoundedQuad(matrices, new Color(40, 40, 40),x,y,x+width,y+h,5,20);
        FontRenderers.getRenderer().drawCenteredString(matrices,title,x+width/2d,y+h/2d-FontRenderers.getRenderer().getMarginHeight()/2d,1f,1f,1f,1f);
    }

    @Override
    public void tickAnim() {

    }


    @Override
    public boolean charTyped(char c, int mods) {
        return false;
    }
}
