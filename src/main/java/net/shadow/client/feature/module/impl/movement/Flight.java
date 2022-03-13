/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.movement;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.BooleanSetting;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.EnumSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Flight extends Module {

    final EnumSetting<FlightMode> mode = this.config.create(new EnumSetting.Builder<>(FlightMode.Static).name("Mode").description("How you fly").get());
    final BooleanSetting bypassVanillaAc = this.config.create(new BooleanSetting.Builder(true).name("Bypass vanilla AC").description("Whether to bypass the vanilla anticheat").get());
    final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(1).name("Speed").description("How fast you fly").min(0).max(10).get());

    int bypassTimer = 0;
    boolean flewBefore = false;

    public Flight() {
        super("Flight", "Allows you to fly without having permission to", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof ClientCommandC2SPacket p && p.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void tick() {
        if (ShadowMain.client.player == null || ShadowMain.client.world == null || ShadowMain.client.getNetworkHandler() == null) {
            return;
        }
        double speed = this.speed.getValue();
        if (bypassVanillaAc.getValue()) {
            bypassTimer++;
            if (bypassTimer > 10) {
                bypassTimer = 0;
                Vec3d p = ShadowMain.client.player.getPos();
                ShadowMain.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y - 0.2, p.z, false));
                ShadowMain.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y + 0.2, p.z, false));
            }
        }
        switch (mode.getValue()) {
            case Vanilla:
                ShadowMain.client.player.getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
                ShadowMain.client.player.getAbilities().flying = true;
                break;
            case Static:
                GameOptions go = ShadowMain.client.options;
                float y = ShadowMain.client.player.getYaw();
                int mx = 0, my = 0, mz = 0;

                if (go.jumpKey.isPressed()) {
                    my++;
                }
                if (go.backKey.isPressed()) {
                    mz++;
                }
                if (go.leftKey.isPressed()) {
                    mx--;
                }
                if (go.rightKey.isPressed()) {
                    mx++;
                }
                if (go.sneakKey.isPressed()) {
                    my--;
                }
                if (go.forwardKey.isPressed()) {
                    mz--;
                }
                double ts = speed / 2;
                double s = Math.sin(Math.toRadians(y));
                double c = Math.cos(Math.toRadians(y));
                double nx = ts * mz * s;
                double nz = ts * mz * -c;
                double ny = ts * my;
                nx += ts * mx * -c;
                nz += ts * mx * -s;
                Vec3d nv3 = new Vec3d(nx, ny, nz);
                ShadowMain.client.player.setVelocity(nv3);
                break;
            case Jetpack:
                if (ShadowMain.client.options.jumpKey.isPressed()) {
                    assert ShadowMain.client.player != null;
                    ShadowMain.client.player.addVelocity(0, speed / 30, 0);
                    Vec3d vp = ShadowMain.client.player.getPos();
                    Random r = new Random();
                    for (int i = 0; i < 10; i++) {
                        ShadowMain.client.world.addImportantParticle(ParticleTypes.SOUL_FIRE_FLAME, true, vp.x, vp.y, vp.z, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125);
                    }
                }
                break;
            case ThreeD:
                ShadowMain.client.player.setVelocity(ShadowMain.client.player.getRotationVector().multiply(speed)
                        .multiply(ShadowMain.client.player.input.pressingForward ? 1 : (ShadowMain.client.player.input.pressingBack ? -1 : 0)));
                break;
        }
    }

    @Override
    public void enable() {
        bypassTimer = 0;
        flewBefore = Objects.requireNonNull(ShadowMain.client.player).getAbilities().flying;
        ShadowMain.client.player.setOnGround(false);
        Objects.requireNonNull(ShadowMain.client.getNetworkHandler()).sendPacket(new ClientCommandC2SPacket(ShadowMain.client.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    public void disable() {
        Objects.requireNonNull(ShadowMain.client.player).getAbilities().flying = flewBefore;
        ShadowMain.client.player.getAbilities().setFlySpeed(0.05f);
    }

    @Override
    public String getContext() {
        return mode.getValue() + "";
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        if (isDebuggerEnabled()) {
            Vec3d a = Utils.getInterpolatedEntityPosition(Objects.requireNonNull(ShadowMain.client.player));
            Vec3d b = a.add(ShadowMain.client.player.getVelocity());
            Renderer.R3D.renderLine(a, b, Color.CYAN, matrices);
        }
    }

    @Override
    public void onHudRender() {

    }

    public enum FlightMode { //  = (MultiValue) this.config.create("Mode", "Static", "Vanilla", "Static", "3D", "Jetpack")
        Vanilla, Static, ThreeD, Jetpack
    }
}
