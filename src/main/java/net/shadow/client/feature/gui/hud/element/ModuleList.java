/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.hud.element;

import java.util.List;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.render.Hud;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.MSAAFramebuffer;
import net.shadow.client.mixin.IDebugHudAccessor;
import net.shadow.client.mixin.IInGameHudAccessor;

public class ModuleList extends HudElement {
    net.shadow.client.feature.module.impl.render.TabGui tgui;

    public ModuleList() {
        super("Modules", 5, 5, 55, 100);
    }


    @Override
    public void renderIntern(MatrixStack stack, double px, double py) {
        stack.push();
        double heightOffsetLeft = 0, heightOffsetRight = 0;
        if (ShadowMain.client.options.debugEnabled) {
            double heightAccordingToMc = 9;
            List<String> lt = ((IDebugHudAccessor) ((IInGameHudAccessor) ShadowMain.client.inGameHud).getDebugHud()).callGetLeftText();
            List<String> rt = ((IDebugHudAccessor) ((IInGameHudAccessor) ShadowMain.client.inGameHud).getDebugHud()).callGetRightText();
            heightOffsetLeft = 2 + heightAccordingToMc * (lt.size() + 3);
            heightOffsetRight = 2 + heightAccordingToMc * rt.size() + 5;
        }
        double oneThird = ShadowMain.client.getWindow().getWidth() / 6;
        if(px < oneThird){
            if(py < heightOffsetLeft){
                stack.translate(0, heightOffsetLeft - py, 0);
            }
        }else if (px > oneThird * 2){
            if(py < heightOffsetRight){
                stack.translate(0, heightOffsetRight- py, 0);
            }
        }
        ModuleRegistry.getByClass(Hud.class).drawModuleList(stack, px, py);
        stack.pop();
    }
}
