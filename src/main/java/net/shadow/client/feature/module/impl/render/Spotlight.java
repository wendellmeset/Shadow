/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.gui.screen.QuickSelectScreen;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

public class Spotlight extends Module {
    public Spotlight() {
        super("Spotlight", "Opens the spotlight menu", ModuleType.RENDER);
    }

    @Override
    public void tick() {
        client.setScreen(new QuickSelectScreen());
        setEnabled(false);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
