/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.CommandRegistry;

import java.awt.*;

public class Help extends Command {

    public Help() {
        super("Help", "Shows all commands", "help", "h", "?", "cmds", "commands", "manual", "man");
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
