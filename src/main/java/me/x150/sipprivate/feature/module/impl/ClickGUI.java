package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {
    int t = 2;

    public ClickGUI() {
        super("ClickGUI", "A visual manager for all modules", ModuleType.RENDER);
    }

    @Override public void tick() {
        t--;
        if (t == 0) {
            SipoverPrivate.client.setScreen(me.x150.sipprivate.feature.gui.clickgui.ClickGUI.instance);
            setEnabled(false);
        }
    }

    @Override public void enable() {
        t = 2;
    }

    @Override public void disable() {
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
