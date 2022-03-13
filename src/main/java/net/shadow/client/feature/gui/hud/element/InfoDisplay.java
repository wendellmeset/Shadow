/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.hud.element;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.feature.module.impl.render.Hud;
import net.shadow.client.helper.font.FontRenderers;

public class InfoDisplay extends HudElement {

    public InfoDisplay() {
        super("Info", 5, 100, 187, 55);
    }

    @Override
    public void renderIntern(MatrixStack stack) {
        stack.push();
        ModuleRegistry.getByClass(Hud.class).drawTopLeft(stack);
        stack.pop();
    }
}
