package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class Hyperspeed extends Module {

    public final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(3).name("Speed").description("The speed multiplier to apply").min(1).max(10).precision(3).get());

    public Hyperspeed() {
        super("Hyperspeed", "Gives you an extreme speed boost", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        //        CoffeeClientMain.client.player.tickMovement();
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
