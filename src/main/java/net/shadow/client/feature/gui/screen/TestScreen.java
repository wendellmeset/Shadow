/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.feature.gui.widget.DataVisualizerWidget;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.render.Renderer;

import java.awt.Color;

public class TestScreen extends ClientScreen implements FastTickable {
    DataVisualizerWidget v;
    Timer u = new Timer();

    @Override
    protected void init() {
        super.init();
//        v = new DataVisualizerWidget(Color.WHITE, true, 50, 1, height - 10, width - 10, 5, 5);
//        addDrawableChild(v);
    }

    @Override
    public void onFastTick() {
//        if (u.hasExpired(500)) {
//            u.reset();
//            v.addDataPoint(Math.random() * 10 - 5);
//        }
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Renderer.R2D.renderQuad(stack, Color.WHITE, 0, 0, width, height);
        Renderer.R2D.renderRoundedQuad(stack, new Color(200, 200, 200), 50, 50, 150, 150, 10, 20);
        Renderer.R2D.renderRoundedShadow(stack, new Color(0, 0, 0, 100), 50, 50, 150, 150, 10, 20, -5);


        super.renderInternal(stack, mouseX, mouseY, delta);
    }

}
