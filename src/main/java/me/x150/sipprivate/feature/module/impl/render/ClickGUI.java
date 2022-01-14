package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {
    public final DoubleSetting radius = this.config.create(new DoubleSetting.Builder(5).name("Round radius").precision(1).min(0).max(10).description("How round the clickgui is").get());
    int t = 2;

    public ClickGUI() {
        super("ClickGUI", "A visual manager for all modules", ModuleType.RENDER);
    }

    @Override
    public void tick() {
        t--;
        if (t == 0) {
            CoffeeClientMain.client.setScreen(me.x150.sipprivate.feature.gui.clickgui.ClickGUI.instance());
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
