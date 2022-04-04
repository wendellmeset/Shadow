/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;

public class Test extends Module {

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_SEND, e -> {
            if (!this.isEnabled()) return;
            PacketEvent event = (PacketEvent) e;
            System.out.println(event.getPacket());
            if (event.getPacket() instanceof ClickSlotC2SPacket uwu) {
                System.out.println(uwu.getSlot() + " <- slot");
                System.out.println(uwu.getButton() + " <- button");
            }
            if (event.getPacket() instanceof ButtonClickC2SPacket uwu) {
                System.out.println(uwu.getButtonId() + " <- Button id");
            }
        });
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

    @Override
    public void tick() {
        client.interactionManager.clickSlot(0, 0, 0, SlotActionType.QUICK_MOVE, client.player);
    }
}
