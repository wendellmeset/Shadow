/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.Random;

public class FloodLuckperms extends Command {
    public FloodLuckperms() {
        super("FloodLuckperms", "Spam luckperms groups", "floodLp", "lpFlood", "floodLuckperms", "luckpermsFlood");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);
        int pp = new IntegerArgumentParser().parse(args[0]);
        Random r = new Random();
        for (int i = 0; i < pp; i++) {
            ShadowMain.client.player.sendChatMessage("/lp creategroup " + i + r.nextInt(100));
        }
    }
}
