/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package net.shadow.client.feature.module.impl.movement;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.CoffeeClientMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

import java.util.Objects;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Always sprints when you walk", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (CoffeeClientMain.client.options.forwardKey.isPressed() && !CoffeeClientMain.client.options.backKey.isPressed() && !CoffeeClientMain.client.player.isSneaking() && !CoffeeClientMain.client.player.horizontalCollision) {
            Objects.requireNonNull(client.player).setSprinting(true);
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

