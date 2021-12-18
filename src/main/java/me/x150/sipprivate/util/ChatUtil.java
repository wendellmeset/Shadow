package me.x150.sipprivate.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtil {
    /**
     * Sends a message to the user, client side
     *
     * @param s The message to send. Formatting using § is allowed
     */
    public static void send(final String s) {
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(s), MinecraftClient.getInstance().player.getUuid());
    }

    public static void sendFormatted(String msg) {
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(Formatting.GRAY + "[" + Formatting.DARK_GRAY + "SipClient" + Formatting.GRAY + "] " + Formatting.RESET + msg), MinecraftClient.getInstance().player.getUuid());
    }
}
