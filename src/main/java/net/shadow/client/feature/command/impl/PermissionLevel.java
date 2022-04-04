/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class PermissionLevel extends Command {
    public PermissionLevel() {
        super("PermissionLevel", "Sets the player's permission level client side", "permissionlevel", "permlevel", "cperm");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"1", "2", "3", "4"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);
        IntegerArgumentParser iap = new IntegerArgumentParser();
        int newPermLevel = iap.parse(args[0]);
        ShadowMain.client.player.setClientPermissionLevel(newPermLevel);
        message("Set the Permission level to [" + newPermLevel + "]");
    }
}
