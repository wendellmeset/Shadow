/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shadow.client.ShadowMain;
import net.shadow.client.helper.util.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class IRCWebSocket extends WebSocketClient {
    public static final List<PlayerEntry> knownIRCPlayers = new CopyOnWriteArrayList<>();
    final String authToken;
    final Runnable onClose;

    public IRCWebSocket(URI serverUri, String authToken, Runnable onClose) {
        super(serverUri, Map.of("Authorization", authToken, "X-MC-UUID", ShadowMain.client.getSession().getUuid(), "X-MC-USERNAME", ShadowMain.client.getSession().getUsername()));
        this.authToken = authToken;
        this.onClose = onClose;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Utils.Logging.success("Connected to IRC");
    }

    @Override
    public void onMessage(String message) {
        Packet p = new Gson().fromJson(message, Packet.class);
        switch (p.id) {
            case "message" -> {
                String uname = p.data.get("who").toString();
                String msg = p.data.get("message").toString();
                Utils.Logging.message(Text.of(String.format("%s[IRC] %s[%s] %s", Formatting.AQUA, Formatting.BLUE, uname, msg)));
            }
            case "userJoined" -> {
                String uname = p.data.get("who").toString();
                Utils.Logging.message(Text.of(String.format("%s[IRC] %s%s joined the IRC", Formatting.AQUA, Formatting.GREEN, uname)));
            }
            case "userLeft" -> {
                String uname = p.data.get("who").toString();
                Utils.Logging.message(Text.of(String.format("%s[IRC] %s%s left the IRC", Formatting.AQUA, Formatting.RED, uname)));
            }
            case "connectFailed" -> {
                String reason = p.data.get("reason").toString();
                Utils.Logging.error("Failed to establish connection, server said: " + reason);
            }
            case "usersList" -> {
                knownIRCPlayers.clear();
                for (LinkedTreeMap<String, String> who : ((Iterable<LinkedTreeMap<String, String>>) p.data.get("who"))) {
                    String u = who.get("username");
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(who.get("uuid"));
                    } catch (Exception ignored) {
                        continue;
                    }
                    PlayerEntry pe = new PlayerEntry(u, uuid);
                    if (!knownIRCPlayers.contains(pe)) knownIRCPlayers.add(pe);
                }

            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Utils.Logging.error("IRC Disconnected");
        this.onClose.run();
        knownIRCPlayers.clear();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public record PlayerEntry(String username, UUID uuid) {

    }

    @AllArgsConstructor
    public static class Packet {
        public String id;
        public Map<String, Object> data;

        public String toRawPacket() {
            return new Gson().toJson(this);
        }

        @Override
        public String toString() {
            return "Packet{" + "id='" + id + '\'' + ", data=" + data + '}';
        }
    }
}
