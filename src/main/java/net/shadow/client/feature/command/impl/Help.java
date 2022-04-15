/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.CommandRegistry;
import net.shadow.client.feature.command.coloring.ArgumentType;

import java.awt.Color;

public class Help extends Command {

    public Help() {
        super("Help", "Shows all commands", "help", "h", "?", "cmds", "commands");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return null;
    }

    @Override
    public void onExecute(String[] args) {
        message("All commands and their description");
        for (Command command : CommandRegistry.getCommands()) {
            message(command.getName() + ": " + command.getDescription());
            message0("  " + String.join(", ", command.getAliases()), Color.GRAY);
        }
    }
}
