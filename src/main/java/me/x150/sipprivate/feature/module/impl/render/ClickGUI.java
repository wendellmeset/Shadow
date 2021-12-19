package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "sexy", ModuleType.RENDER);
    }
    int t = 2;

    @Override public void tick() {
        t--;
        if (t == 0) {
            SipoverPrivate.client.setScreen(new me.x150.sipprivate.feature.gui.clickgui.ClickGUI());
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
