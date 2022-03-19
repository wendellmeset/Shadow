/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.hud.element;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.render.Hud;

public class InfoHUD extends HudElement {
    public InfoHUD() {
        super("Info", 5, 5, 180, 57);
    }


    @Override
    public void renderIntern(MatrixStack stack) {
        stack.push();
        ModuleRegistry.getByClass(Hud.class).drawTopLeft(stack);
        stack.pop();
    }
}
