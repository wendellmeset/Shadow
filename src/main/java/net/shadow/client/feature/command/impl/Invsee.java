/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;

import java.util.Objects;

public class Invsee extends Command {

    public Invsee() {
        super("Invsee", "Shows you the inventory of another player", "invsee", "isee");
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
        if (args.length == 0) {
            message("i need username");
            return;
        }
        PlayerEntity t = null;
        for (Entity player : Objects.requireNonNull(ShadowMain.client.world).getEntities()) {
            if (player instanceof PlayerEntity player1) {
                if (player1.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                    t = player1;
                    break;
                }
            }
        }
        if (t == null) {
            error("No player found");
            return;
        }
        PlayerEntity finalT = t;
        Utils.TickManager.runOnNextRender(() -> ShadowMain.client.setScreen(new InventoryScreen(finalT)));
    }
}
