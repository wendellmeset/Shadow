/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.fun;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.Rotations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.Objects;

public class Spinner extends Module {

    final double r = 0;
    //    final SliderValue speed = (SliderValue) this.config.create("Timeout", 5, 0, 100, 0).description("How much to wait between rotations");
    final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(5)
            .name("Delay")
            .description("How much to wait when spinning")
            .min(0)
            .max(100)
            .precision(0)
            .get());
    int timeout = 0;

    public Spinner() {
        super("Spinner", "Spins around like a maniac and throws whatever you have", ModuleType.FUN);
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
    public void onFastTick() {
        timeout--; // decrease timeout
        if (timeout > 0) {
            return; // if timeout isn't expired, do nothing
        }
        timeout = (int) Math.floor(speed.getValue()); // timeout expired, set it back to full
        Rotations.setClientPitch((float) ((Math.random() * 60) - 30));
        Rotations.setClientYaw((float) (Math.random() * 360));
        PlayerInteractItemC2SPacket p = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND);
        Objects.requireNonNull(CoffeeClientMain.client.getNetworkHandler()).sendPacket(p);
        PlayerMoveC2SPacket p1 = new PlayerMoveC2SPacket.LookAndOnGround((float) r, Rotations.getClientPitch(), Objects.requireNonNull(CoffeeClientMain.client.player).isOnGround());
        CoffeeClientMain.client.getNetworkHandler().sendPacket(p1);
    }

    @Override
    public void onHudRender() {

    }
}

