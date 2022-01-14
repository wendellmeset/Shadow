/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.util.Objects;

public class EdgeJump extends Module {

    public EdgeJump() {
        super("EdgeJump", "Jumps automatically at the edges of blocks", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.world == null) {
            return;
        }
        if (!CoffeeClientMain.client.player.isOnGround() || CoffeeClientMain.client.player.isSneaking()) {
            return;
        }

        Box bounding = CoffeeClientMain.client.player.getBoundingBox();
        bounding = bounding.offset(0, -0.5, 0);
        bounding = bounding.expand(-0.001, 0, -0.001);
        if (!CoffeeClientMain.client.world.getBlockCollisions(client.player, bounding).iterator().hasNext()) {
            Objects.requireNonNull(client.player).jump();
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
