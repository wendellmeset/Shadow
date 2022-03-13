/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

public class KickSelf extends Command {
    public KickSelf() {
        super("KickSelf", "kick yourself from the server", "kickself");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return new String[]{"quit", "chars", "packet", "self", "spam", "packets"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 1) {
            message("Please use the format >kickself <quit/chars/packet/self/spam/packets>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "spam":
                for (int i = 0; i < 50; i++) {
                    ShadowMain.client.player.sendChatMessage("/");
                }
                break;

            case "quit":
                ShadowMain.client.world.disconnect();
                break;

            case "chars":
                ShadowMain.client.player.networkHandler.sendPacket(new ChatMessageC2SPacket("\u00a7"));
                break;

            case "packet":
                ShadowMain.client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(3.1e7, 100, 3.1e7, false));
                break;

            case "self":
                PlayerInteractEntityC2SPacket h = PlayerInteractEntityC2SPacket.attack(ShadowMain.client.player, false);
                ShadowMain.client.player.networkHandler.sendPacket(h);
                break;

            case "packets":
                for (int i = 0; i < 5000; i++) {
                    ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                }
                break;
        }
    }
}
