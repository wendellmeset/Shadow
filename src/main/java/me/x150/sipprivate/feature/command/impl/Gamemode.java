/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import net.minecraft.world.GameMode;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Gamemode extends Command {

    public Gamemode() {
        super("Gamemode", "Switch gamemodes client side", "gamemode", "gm", "gmode", "gamemodespoof", "gmspoof");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(GameMode.values()).map(GameMode::getName).collect(Collectors.toList()).toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (CoffeeClientMain.client.interactionManager == null) {
            return;
        }
        if (args.length == 0) {
            message("gamemode pls");
        } else {
            GameMode gm = GameMode.byName(args[0]);
            CoffeeClientMain.client.interactionManager.setGameMode(gm);
        }
    }
}
