/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.Keybind;
import me.x150.sipprivate.helper.Rotations;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class FreeLook extends Module {

    //    final BooleanValue hold        = (BooleanValue) this.config.create("Hold", true).description("Whether or not to disable the module when the keybind is unpressed");
//    final  BooleanValue enableAA    = (BooleanValue) this.config.create("Enable Anti-Aim", false).description("hvh toggle rage nn noob");
//    final  MultiValue   aaMode      = this.config.create("AA mode", "Spin", "Spin", "Jitter", "Sway");
//    final  SliderValue  aaSpeed     = this.config.create("Anti-Aim Speed", 1f, 0.1f, 6f, 1);
//    final  SliderValue  jitterRange = this.config.create("Jitter Range", 90, 15, 90, 0);
//    final  SliderValue  swayRange   = this.config.create("Sway Range", 45, 15, 60, 0);
    final BooleanSetting hold = this.config.create(new BooleanSetting.Builder(true)
            .name("Hold")
            .description("Disables the module after you unpress the keybind")
            .get());
    final BooleanSetting enableAA = this.config.create(new BooleanSetting.Builder(false)
            .name("Enable Anti-Aim")
            .description("Hvh toggle rage nn noob")
            .get());
    final EnumSetting<AntiAimMode> aaMode = this.config.create(new EnumSetting.Builder<>(AntiAimMode.Spin)
            .name("AA Mode")
            .description("How to aim")
            .get());
    final DoubleSetting aaSpeed = this.config.create(new DoubleSetting.Builder(1)
            .name("AA Speed")
            .description("How fast to aim")
            .min(0.1)
            .max(6)
            .precision(1)
            .get());
    final DoubleSetting jitterRange = this.config.create(new DoubleSetting.Builder(90)
            .name("Jitter range")
            .description("How far to jitter")
            .min(15)
            .max(90)
            .precision(0)
            .get());
    final DoubleSetting swayRange = this.config.create(new DoubleSetting.Builder(45)
            .name("Sway range")
            .description("How far to sway")
            .min(15)
            .max(60)
            .precision(0)
            .get());
    public float newyaw, newpitch, oldyaw, oldpitch;
    Perspective before = Perspective.FIRST_PERSON;
    Keybind kb;
    int jittertimer = 0;
    int swayYaw = 0;

    public FreeLook() {
        super("FreeLook", "The lunar freelook but without the restrictions", ModuleType.RENDER);
        aaMode.showIf(enableAA::getValue);
        aaSpeed.showIf(() -> aaMode.getValue() != AntiAimMode.Jitter && enableAA.getValue());
        jitterRange.showIf(() -> aaMode.getValue() == AntiAimMode.Jitter && enableAA.getValue());
        swayRange.showIf(() -> aaMode.getValue() == AntiAimMode.Sway && enableAA.getValue());
    }

    @Override
    public void tick() {
        if (kb == null) {
            return;
        }
        if (!kb.isPressed() && hold.getValue()) {
            this.setEnabled(false);
        }

        Rotations.setClientPitch(newpitch);
        Rotations.setClientYaw(newyaw);
    }

    @Override
    public void enable() {
        kb = new Keybind((int) (keybind.getValue() + 0));
        before = client.options.getPerspective();
        oldyaw = Objects.requireNonNull(client.player).getYaw();
        oldpitch = client.player.getPitch();
        newyaw = client.player.getYaw();
        if (enableAA.getValue()) {
            newpitch = 90;
        } else {
            newpitch = client.player.getPitch();
        }
    }

    @Override
    public void disable() {
        client.options.setPerspective(before);
        Objects.requireNonNull(client.player).setYaw(oldyaw);
        client.player.setPitch(oldpitch);
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Override
    public void onHudRender() {

    }

    @Override
    public void onFastTick() {
        if (!enableAA.getValue()) {
            return;
        }
        switch (aaMode.getValue()) {
            case Spin -> newyaw = (float) MathHelper.wrapDegrees(newyaw + aaSpeed.getValue());
            case Jitter -> {
                int temp = (int) (jitterRange.getValue() + 0);
                if (jittertimer == 1) {
                    temp *= -1;
                }
                if (jittertimer >= 1) {
                    jittertimer = -1;
                }
                jittertimer++;
                newyaw = MathHelper.wrapDegrees(Objects.requireNonNull(client.player).getYaw() + 180 + temp);
            }
            case Sway -> {
                int temp = swayYaw;
                if (temp >= swayRange.getValue() * 2) {
                    temp = (int) (swayRange.getValue() + 0) - (swayYaw - (int) (swayRange.getValue() * 2));
                } else {
                    temp = (int) (swayRange.getValue() * -1) + swayYaw;
                }
                if (swayYaw >= swayRange.getValue() * 4) {
                    swayYaw = 0;
                }
                swayYaw += aaSpeed.getValue();
                newyaw = MathHelper.wrapDegrees(Objects.requireNonNull(client.player).getYaw() + 180 + temp);
            }
        }
        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(newyaw, newpitch, Objects.requireNonNull(client.player).isOnGround()));
    }

    public enum AntiAimMode {
        Spin, Jitter, Sway
    }
}

