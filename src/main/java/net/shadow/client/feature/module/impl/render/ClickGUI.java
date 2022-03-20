/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.StringSetting;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.feature.gui.clickgui.element.impl.config.DoubleSettingEditor;
import net.shadow.client.feature.gui.clickgui.element.impl.config.StringSettingEditor;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.gui.panels.elements.PanelButton;
import net.shadow.client.feature.gui.panels.elements.PanelFrame;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.feature.module.NoNotificationDefault;

@NoNotificationDefault
public class ClickGUI extends Module {
    public final DoubleSetting radius = this.config.create(new DoubleSetting.Builder(5).name("Round radius").precision(1).min(0).max(10).description("How round the clickgui is").get());
    DoubleSetting dub = new DoubleSetting(10D, "realDouble", "some dumbass shit", 1, 0, 100);
    StringSetting str = new StringSetting("real", "super string", "");
    int t = 2;

    public ClickGUI() {
        super("ClickGUI", "A visual manager for all modules", ModuleType.RENDER);
    }

    @Override
    public void tick() {
        t--;
        if (t == 0) {
            ShadowMain.client.setScreen(net.shadow.client.feature.gui.clickgui.ClickGUI.instance());
            setEnabled(false);
        }
    }

    @Override
    public void enable() {
        t = 2;
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
