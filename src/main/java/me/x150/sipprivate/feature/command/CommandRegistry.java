/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command;

import me.x150.sipprivate.feature.command.impl.Bind;
import me.x150.sipprivate.feature.command.impl.Config;
import me.x150.sipprivate.feature.command.impl.ConfigUtils;
import me.x150.sipprivate.feature.command.impl.Drop;
import me.x150.sipprivate.feature.command.impl.Effect;
import me.x150.sipprivate.feature.command.impl.FakeItem;
import me.x150.sipprivate.feature.command.impl.Find;
import me.x150.sipprivate.feature.command.impl.ForEach;
import me.x150.sipprivate.feature.command.impl.Gamemode;
import me.x150.sipprivate.feature.command.impl.Help;
import me.x150.sipprivate.feature.command.impl.Hologram;
import me.x150.sipprivate.feature.command.impl.Invsee;
import me.x150.sipprivate.feature.command.impl.Kill;
import me.x150.sipprivate.feature.command.impl.Panic;
import me.x150.sipprivate.feature.command.impl.Plugins;
import me.x150.sipprivate.feature.command.impl.RageQuit;
import me.x150.sipprivate.feature.command.impl.Rename;
import me.x150.sipprivate.feature.command.impl.Say;
import me.x150.sipprivate.feature.command.impl.SkinExploit;
import me.x150.sipprivate.feature.command.impl.Taco;
import me.x150.sipprivate.feature.command.impl.Test;
import me.x150.sipprivate.feature.command.impl.Toggle;
import me.x150.sipprivate.feature.command.impl.ViewNbt;
import me.x150.sipprivate.feature.gui.screen.CoffeeConsoleScreen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry {

    private static final List<Command> commands = new ArrayList<>();

    static {
        // TODO: 18.12.21 add commands
        init();
    }

    public static void init() {
        commands.clear();
        commands.add(new Toggle());
        commands.add(new Config());
        commands.add(new Gamemode());
        commands.add(new Effect());
        commands.add(new Hologram());
        commands.add(new Help());
        commands.add(new ForEach());
        commands.add(new Drop());
        commands.add(new Panic());
        commands.add(new Rename());
        commands.add(new ViewNbt());
        commands.add(new Say());
        commands.add(new ConfigUtils());
        commands.add(new Kill());
        commands.add(new Invsee());
        commands.add(new RageQuit());
        commands.add(new Plugins());
        commands.add(new Find());
        commands.add(new FakeItem());
        commands.add(new Taco());
        commands.add(new Bind());
        commands.add(new SkinExploit());
        commands.add(new Test());
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
            CoffeeConsoleScreen.instance().log("Command \"" + cmd + "\" not found", CoffeeConsoleScreen.ERROR);
        } else {
            CoffeeConsoleScreen.instance().log("> " + command, CoffeeConsoleScreen.CLIENT);
            try {
                c.onExecute(args);
            } catch (Exception e) {
                CoffeeConsoleScreen.instance().log("Error while running command " + command, CoffeeConsoleScreen.ERROR);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                for (String s : sw.toString().split("\n")) {
                    CoffeeConsoleScreen.instance().log(s, CoffeeConsoleScreen.BACKGROUND);
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
