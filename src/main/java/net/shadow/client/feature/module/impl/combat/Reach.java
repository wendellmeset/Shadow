/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.combat;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

public class Reach extends Module {
    // TODO: 10.04.22 finish this WHERE TF THE MIXIN GO????????

    final DoubleSetting reachDist = this.config.create(new DoubleSetting.Builder(3).min(3).max(10).precision(1).name("Distance").description("How far to reach").get());

    public Reach() {
        super("Reach", "Reach further", ModuleType.COMBAT);
    }

    @Override
    public void tick() {

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

    public double getReachDistance() {
        return reachDist.getValue();
    }
}
