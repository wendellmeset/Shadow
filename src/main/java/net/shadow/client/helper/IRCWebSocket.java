/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shadow.client.ShadowMain;
import net.shadow.client.helper.util.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class IRCWebSocket extends WebSocketClient {
    @AllArgsConstructor
    public static
    class Packet {
        String id;
        Map<String, Object> data;

        public String toRawPacket() {
            return new Gson().toJson(this);
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "id='" + id + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
    String authToken;
    Runnable onClose;
    public IRCWebSocket(URI serverUri, String authToken, Runnable onClose) {
        super(serverUri, Map.of("Authorization", authToken));
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
        switch(p.id) {
            case "message" -> {
                String uname = p.data.get("who").toString();
                String msg = p.data.get("message").toString();
                ShadowMain.client.player.sendMessage(Text.of(String.format("%s[IRC] %s[%s] %s", Formatting.AQUA, Formatting.BLUE, uname, msg)), false);
            }
            case "userJoined" -> {
                String uname = p.data.get("who").toString();
                ShadowMain.client.player.sendMessage(Text.of(String.format("%s[IRC] %s%s joined the IRC", Formatting.AQUA, Formatting.GREEN, uname)), false);
            }
            case "userLeft" -> {
                String uname = p.data.get("who").toString();
                ShadowMain.client.player.sendMessage(Text.of(String.format("%s[IRC] %s%s left the IRC", Formatting.AQUA, Formatting.RED, uname)), false);
            }
            case "connectFailed" -> {
                String reason = p.data.get("reason").toString();
                Utils.Logging.error("Failed to establish connection, server said: "+reason);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Utils.Logging.error("IRC Disconnected");
        this.onClose.run();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
