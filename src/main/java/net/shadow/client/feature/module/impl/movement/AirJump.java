/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package net.shadow.client.feature.module.impl.movement;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.CoffeeClientMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

public class AirJump extends Module {

    public AirJump() {
        super("AirJump", "Allows you to jump mid air", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (CoffeeClientMain.client.options.jumpKey.isPressed()) {
            CoffeeClientMain.client.player.setOnGround(true);
            CoffeeClientMain.client.player.fallDistance = 0f;
        }
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
