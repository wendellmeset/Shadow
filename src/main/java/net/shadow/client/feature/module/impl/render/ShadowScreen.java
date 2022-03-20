/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.StringSetting;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.feature.gui.clickgui.element.impl.config.DoubleSettingEditor;
import net.shadow.client.feature.gui.clickgui.element.impl.config.StringSettingEditor;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.gui.panels.elements.PanelButton;
import net.shadow.client.feature.gui.panels.elements.PanelFrame;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.util.Transitions;

public class ShadowScreen extends Module {

    DoubleSetting ds = new DoubleSetting(4D, "RealDouble", "shid", 1, 1, 100);
    StringSetting ss = new StringSetting("rea", "real", "shid");

    public ShadowScreen() {
        super("Tools", "tools screen", ModuleType.RENDER);
    }

    @Override
    public void tick() {
    }

    @Override
    public void enable() {
        PanelsGui menu = new PanelsGui(new PanelFrame[]{
            new PanelFrame(100, 100, 200, 300, "Grief", new Element[]{
                new DoubleSettingEditor(0, 0, -1, ds),
                new StringSettingEditor(0, 25, 100, ss),
                new PanelButton(0, 50, -1, "real shid", () -> {

                })
            })
        });

        ShadowMain.client.setScreen(menu);
    }

    @Override
    public void disable() {
    }

    @Override
    public void onFastTick() {
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
