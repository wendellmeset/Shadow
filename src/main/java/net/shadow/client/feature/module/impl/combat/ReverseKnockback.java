/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.combat;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventListener;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class ReverseKnockback extends Module {

    public ReverseKnockback() {
        super("ReverseKnockback", "reverse the knockback you deal", ModuleType.MISC);
        Events.registerEventHandlerClass(this);
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
    public void onHudRender() {

    }

    @EventListener(type=EventType.PACKET_SEND)
    void packetSend(PacketEvent event){
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
            if (!(packet instanceof PlayerMoveC2SPacket.LookAndOnGround || packet instanceof PlayerMoveC2SPacket.Full))
                return;
            event.setCancelled(true);
            double x = packet.getX(0);
            double y = packet.getY(0);
            double z = packet.getZ(0);

            Packet<?> newPacket;
            if (packet instanceof PlayerMoveC2SPacket.Full) {
                newPacket = new PlayerMoveC2SPacket.Full(x, y, z, MathHelper.wrapDegrees(client.player.getYaw() + 180), 0, packet.isOnGround());
            } else {
                newPacket = new PlayerMoveC2SPacket.LookAndOnGround(MathHelper.wrapDegrees(client.player.getYaw() + 180), 0, packet.isOnGround());
            }

            client.player.networkHandler.getConnection().send(newPacket);
        }
        if(event.getPacket() instanceof PlayerInteractEntityC2SPacket){
            client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }
}
