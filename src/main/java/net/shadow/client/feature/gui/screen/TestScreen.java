/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import net.shadow.client.helper.render.Renderer;

public class TestScreen extends ClientScreen {
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        Vec2f[] points = new Vec2f[] {
            new Vec2f(0,0),
            new Vec2f(100,100),
            new Vec2f(mouseX,mouseY),
                new Vec2f(500,300),
                new Vec2f(200,200),
                new Vec2f(600,200)
        };
        Renderer.R2D.renderBezierCurve(stack,points,1f,1f,1f,1f,0.01f);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

}
