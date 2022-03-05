/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.coffee.feature.module.impl.movement;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.config.DoubleSetting;
import me.x150.coffee.feature.config.EnumSetting;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;


public class Jesus extends Module {

    public final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Solid).name("Mode").description("How to keep you up").get());
    final DoubleSetting velStrength = this.config.create(new DoubleSetting.Builder(0.1).name("Velocity strength").description("How much velocity to apply").min(0.001).max(0.3).precision(3)
            .get());

    public Jesus() {
        super("Jesus", "Allows you to walk on water", ModuleType.MOVEMENT);
        velStrength.showIf(() -> mode.getValue() == Mode.Velocity);
    }

    @Override
    public void tick() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (CoffeeClientMain.client.player.isWet()) {
            switch (mode.getValue()) {
                case Jump -> Objects.requireNonNull(client.player).jump();
                case Velocity -> Objects.requireNonNull(client.player).setVelocity(client.player.getVelocity().x, velStrength.getValue(), client.player.getVelocity().z);
                case Legit -> Objects.requireNonNull(client.player).addVelocity(0, 0.03999999910593033, 0); // LivingEntity:1978, vanilla velocity
            }
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

    public enum Mode {
        Solid, Jump, Velocity, Legit
    }
}

