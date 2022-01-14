/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import me.x150.sipprivate.helper.event.events.PlayerNoClipQueryEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Freecam extends Module {

    //    final SliderValue speed = (SliderValue) this.config.create("Speed", 1, 0, 10, 1).description("The speed to fly with");
    final DoubleSetting speed = this.config.create(new DoubleSetting.Builder(1)
            .name("Speed")
            .description("The speed to fly with")
            .min(0)
            .max(10)
            .precision(1)
            .get());
    Vec3d startloc;
    float pitch = 0f;
    float yaw = 0f;
    boolean flewBefore;

    public Freecam() {
        super("Freecam", "Imitates spectator without you having permission to use it", ModuleType.RENDER);
        Events.registerEventHandler(EventType.PACKET_SEND, event1 -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof PlayerMoveC2SPacket) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof PlayerInputC2SPacket) {
                event.setCancelled(true);
            }
        });
        Events.registerEventHandler(EventType.NOCLIP_QUERY, event -> {
            if (!this.isEnabled() || ((PlayerNoClipQueryEvent) event).getPlayer().isOnGround()) {
                return;
            }
            ((PlayerNoClipQueryEvent) event).setNoClipState(PlayerNoClipQueryEvent.NoClipState.ACTIVE);
        });
    }

    @Override
    public void tick() {
        Objects.requireNonNull(client.player).getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
        client.player.getAbilities().flying = true;
    }

    @Override
    public void enable() {
        startloc = Objects.requireNonNull(client.player).getPos();
        pitch = client.player.getPitch();
        yaw = client.player.getYaw();
        client.gameRenderer.setRenderHand(false);
        flewBefore = client.player.getAbilities().flying;
        client.player.setOnGround(false);
    }

    @Override
    public void disable() {
        if (startloc != null) {
            Objects.requireNonNull(client.player).updatePosition(startloc.x, startloc.y, startloc.z);
        }
        startloc = null;
        Objects.requireNonNull(client.player).setYaw(yaw);
        client.player.setPitch(pitch);
        yaw = pitch = 0f;
        client.gameRenderer.setRenderHand(true);
        client.player.getAbilities().flying = flewBefore;
        client.player.getAbilities().setFlySpeed(0.05f);
        client.player.setVelocity(0, 0, 0);
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        Objects.requireNonNull(client.player).setSwimming(false);
        client.player.setPose(EntityPose.STANDING);
    }

    @Override
    public void onHudRender() {

    }
}

