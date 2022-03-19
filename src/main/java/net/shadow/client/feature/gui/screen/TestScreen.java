/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.font.FontRenderers;

public class TestScreen extends ClientScreen {

    public TestScreen() {
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        FontRenderers.getRenderer().drawString(stack, "among us", mouseX, mouseY, 1f, 1f, 1f, 1f);
        FontRenderers.getMono().drawString(stack, "among us", mouseX, mouseY + FontRenderers.getRenderer().getMarginHeight(), 1f, 1f, 1f, 1f);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

}
