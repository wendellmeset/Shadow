/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.world.GameMode;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Arrays;

public class Gamemode extends Command {

    public Gamemode() {
        super("Gamemode", "Switch gamemodes client side", "gamemode", "gm", "gmode", "gamemodespoof", "gmspoof");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(GameMode.values()).map(GameMode::getName).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (ShadowMain.client.interactionManager == null) {
            return;
        }
        if (args.length == 0) {
            message("gamemode pls");
        } else {
            GameMode gm = GameMode.byName(args[0]);
            ShadowMain.client.interactionManager.setGameMode(gm);
        }
    }
}
