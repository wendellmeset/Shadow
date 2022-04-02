/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;

import java.lang.reflect.Field;

public class Test extends Module {

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) return;
            PacketEvent pe = (PacketEvent) event;
            Packet<?> p = pe.getPacket();
            if (p instanceof PlayerMoveC2SPacket) return;
            System.out.println("-> "+p.getClass().getSimpleName());
            for (Field declaredField : p.getClass().getDeclaredFields()) {
                try {
                    declaredField.setAccessible(true);
                    Object val = declaredField.get(p);
                    System.out.println("    "+declaredField.getName()+": "+val.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    final Timer timer = new Timer();
    @Override
    public void tick() {
        if (timer.hasExpired(500)) {
            timer.reset();
            Notification.create(3000, "Among ".repeat(5), Notification.Type.values()[(int) Math.floor(Math.random() * Notification.Type.values().length)], "Us!!!!!! ".repeat((int) Math.floor(Math.random() * 10)));
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
