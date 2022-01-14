/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.misc;

import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;

public class Timer extends Module {

    //    final SliderValue newTps = this.config.create("New TPS", 20, 0.1, 100, 1);
    final DoubleSetting newTps = this.config.create(new DoubleSetting.Builder(20)
            .name("New TPS")
            .description("To what to set the new tps to")
            .min(0.1)
            .max(100)
            .precision(1)
            .get());

    public Timer() {
        super("Timer", "Changes the speed of the game client side", ModuleType.MISC);
    }

    @Override
    public void tick() {
        Utils.setClientTps((float) (newTps.getValue() + 0d));
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        Utils.setClientTps(20f);
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

