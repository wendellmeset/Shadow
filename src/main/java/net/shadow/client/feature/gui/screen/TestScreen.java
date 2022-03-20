/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.GifPlayer;
import net.shadow.client.helper.font.FontRenderers;

import java.io.File;

public class TestScreen extends ClientScreen {
    private static GifPlayer gp = GifPlayer.createFromFile(new File("/home/x150/Downloads/img.gif"),30);
    public TestScreen() {

    }

    @Override
    protected void init() {
        gp.setGifFile(new File("/home/x150/Downloads/img.gif"));
        super.init();
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        gp.renderFrame(stack,10,10,300,300);
//        FontRenderers.getRenderer().drawString(stack, "among us", mouseX, mouseY, 1f, 1f, 1f, 1f);
//        FontRenderers.getMono().drawString(stack, "among us", mouseX, mouseY + FontRenderers.getRenderer().getMarginHeight(), 1f, 1f, 1f, 1f);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

}
