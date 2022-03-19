/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.network.ClientPlayerEntity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.DoubleArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class Vclip extends Command {
    public Vclip() {
        super("Vclip", "veritcal clip", "vclip");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) return new String[]{"(distance)"};
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);

        ClientPlayerEntity player = ShadowMain.client.player;
        player.updatePosition(player.getX(),
                player.getY() + new DoubleArgumentParser().parse(args[1]), player.getZ());
    }
}
