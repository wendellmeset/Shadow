/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.movement;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.feature.config.*;
import net.shadow.client.feature.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class BoatFly extends Module {

    final BooleanSetting turn = this.config.create(new BooleanSetting.Builder(true).name("Turn").description("Turn the boat with your mouse cursor").get());
    final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(1).min(1).max(3).precision(1).name("Speed").description("Speed of the boat").get());

    public BoatFly() {
        super("BoatFly", "get real", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {
        try {
            Entity vehicle = client.player.getVehicle();
            if (vehicle == null) return;
            Vec3d velocity = vehicle.getVelocity();
            double motionY = client.options.jumpKey.isPressed() ? (speed.getValue() / 3) : 0;
            vehicle.setVelocity(new Vec3d(velocity.x, motionY, velocity.z));
            if (client.options.jumpKey.isPressed()) {
                Vec3d forward = Vec3d.fromPolar(0, client.player.getYaw()).normalize();
                vehicle.setVelocity(forward.x * speed.getValue(), motionY, forward.z * speed.getValue());
            }
            if (turn.getValue()) {
                client.player.getVehicle().setYaw(client.player.getYaw());
            }
        } catch (Exception ignored) {
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
