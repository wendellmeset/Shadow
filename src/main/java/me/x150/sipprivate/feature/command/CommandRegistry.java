/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command;

import me.x150.sipprivate.feature.gui.AtomicConsoleScreen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry {

    private static final List<Command> commands = new ArrayList<>();

    static {
        // TODO: 18.12.21 add commands 
    }

    public static void init() {

    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static void execute(String command) {
        String[] spl = command.split(" +");
        String cmd = spl[0].toLowerCase();
        String[] args = Arrays.copyOfRange(spl, 1, spl.length);
        Command c = CommandRegistry.getByAlias(cmd);
        if (c == null) {
            AtomicConsoleScreen.instance().log("Command \"" + cmd + "\" not found", AtomicConsoleScreen.ERROR);
        } else {
            AtomicConsoleScreen.instance().log("> " + command, AtomicConsoleScreen.CLIENT);
            try {
                c.onExecute(args);
            } catch (Exception e) {
                AtomicConsoleScreen.instance().log("Error while running command " + command, AtomicConsoleScreen.ERROR);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                for (String s : sw.toString().split("\n")) {
                    AtomicConsoleScreen.instance().log(s, AtomicConsoleScreen.BACKGROUND);
                }
            }
        }
    }

    public static Command getByAlias(String n) {
        for (Command command : getCommands()) {
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(n)) {
                    return command;
                }
            }
        }
        return null;
    }
}
