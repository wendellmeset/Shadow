/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.world.GameMode;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.Globals;
import net.shadow.client.mixin.SessionAccessor;

import java.net.InetSocketAddress;
import java.util.Objects;

public class ForceOP extends Command {
    public ForceOP() {
        super("ForceOP", "get op on cracked servers", "forceop", "crackedop");
    }

    private static void authUsername(String username) {
        ShadowMain.client.getSessionProperties().clear();
        ((SessionAccessor) ShadowMain.client).setUsername(username);
    }

    private String getPlayer() {
        for (PlayerListEntry player : ShadowMain.client.getNetworkHandler().getPlayerList()) {
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) {
                return player.getProfile().getName();
            }
        }
        return "None";
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        InetSocketAddress socket = (InetSocketAddress) ShadowMain.client.player.networkHandler.getConnection().getAddress();
        Globals.serverAddress = new ServerAddress(socket.getHostName(), socket.getPort());
        String nick;
        if (args.length == 0) {
            nick = getPlayer();
            if (nick.equals("None")) {
                error("Could not find a suitable OP Player, use arguments to define");
                return;
            }
        } else {
            nick = args[0];
        }
        authUsername(nick);
        Globals.reconnectInstantly = true;
        ShadowMain.client.world.disconnect();
    }
}
