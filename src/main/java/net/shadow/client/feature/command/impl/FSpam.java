/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.Arrays;

public class FSpam extends Command {
    public FSpam() {
        super("FSpam", "fast spammer", "fspam", "fastspam", "quickspam");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(amount)"};
        }
        if (args.length > 1) {
            return new String[]{"(message)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 2);
        int amount = new IntegerArgumentParser().parse(args[0]);
        for (int i = 0; i < amount; i++) {
            ShadowMain.client.player.sendChatMessage(String.join("", Arrays.copyOfRange(args, 1, args.length)));
        }
    }
}
