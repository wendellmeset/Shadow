/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class CPerm extends Command {
    public CPerm() {
        super("ClientPermission", "set client permission levels", "clientpermission", "cperm");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(permission level)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);
        IntegerArgumentParser iap = new IntegerArgumentParser();
        int uwu = iap.parse(args[0]);
        ShadowMain.client.player.setClientPermissionLevel(uwu);
        message("Set the Permission level to [" + uwu + "]");
    }
}
