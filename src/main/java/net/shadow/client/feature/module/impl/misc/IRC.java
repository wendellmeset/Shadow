/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.IRCWebSocket;
import net.shadow.client.helper.ShadowAPIWrapper;
import net.shadow.client.helper.event.EventListener;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.util.Utils;

import java.net.URI;

public class IRC extends Module {
    static String ircPrefix = "#";
    public IRCWebSocket wsS;

    public IRC() {
        super("IRC", "Chat with others using the client", ModuleType.MISC);
    }

    @EventListener(type = EventType.PACKET_SEND)
    void onPackSent(PacketEvent pe) {
        if (pe.getPacket() instanceof ChatMessageC2SPacket msg) {
            String m = msg.getChatMessage();
            if (m.startsWith(ircPrefix)) {
                pe.setCancelled(true);
                wsS.send(m.substring(ircPrefix.length()).trim());
            }
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        if (ShadowAPIWrapper.getAuthKey() == null) {
            Utils.Logging.error("Cannot use IRC because you didn't use the launcher to launch shadow.");
            setEnabled(false);
            return;
        }
        initSock();
    }

    void initSock() {
        this.wsS = new IRCWebSocket(URI.create(ShadowAPIWrapper.BASE_WS + "/irc"), ShadowAPIWrapper.getAuthKey(), () -> {
            this.wsS = null;
            if (this.isEnabled())
                this.setEnabled(false);
        });
        this.wsS.connect();
    }

    @Override
    public void disable() {
        if (this.wsS != null)
            this.wsS.close();
    }

    public void reconnect() {
        if (this.wsS != null)
            this.wsS.close();
        initSock();
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
