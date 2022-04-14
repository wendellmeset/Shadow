/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.PlayerFromNameArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;
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
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Provide target username");
        PlayerEntity t = new PlayerFromNameArgumentParser(true).parse(args[0]);
        Utils.TickManager.runOnNextRender(() -> ShadowMain.client.setScreen(new InventoryScreen(t)));
    }
}
