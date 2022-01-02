package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class Hyperspeed extends Module {

    public DoubleSetting speed = this.config.create(new DoubleSetting.Builder(3).name("Speed").description("The speed multiplier to apply").min(1).max(100).precision(1).get());

    public Hyperspeed() {
        super("Hyperspeed", "Makes you go weee extemely fast", ModuleType.MOVEMENT);
    }

    @Override public void tick() {
        //        CoffeeClientMain.client.player.tickMovement();
    }

    @Override public void enable() {

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
