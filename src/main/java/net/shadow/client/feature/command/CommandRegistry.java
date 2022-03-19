/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command;

import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.feature.command.impl.*;
import net.shadow.client.helper.util.Utils;

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
        commands.add(new Kickall());
        commands.add(new ItemExploit());
        commands.add(new Inject());
        commands.add(new Apvel());
        commands.add(new AsConsole());
        commands.add(new Author());
        commands.add(new Ban());
        commands.add(new Boot());
        commands.add(new CheckCmd());
        commands.add(new ClientFlood());
        commands.add(new CPerm());
        commands.add(new Crash());
        commands.add(new Damage());
        commands.add(new Equip());
        commands.add(new EVclip());
        commands.add(new FloodLP());
        commands.add(new ForceOP());
        commands.add(new ItemSpoof());
        commands.add(new HClip());
        commands.add(new Image());
        commands.add(new ItemData());
        commands.add(new KickSelf());
        commands.add(new Lag());
        commands.add(new LinkPlayer());
        commands.add(new Open());
        commands.add(new Poof());
        commands.add(new SpawnData());
        commands.add(new Stop());
        commands.add(new Vclip());
        commands.add(new FSpam());
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
            Utils.Logging.error("Command \"" + cmd + "\" not found");
        } else {
            //            CoffeeConsoleScreen.instance().log("> " + command, CoffeeConsoleScreen.CLIENT);
            try {
                c.onExecute(args);
            } catch (CommandException cex) {
                Utils.Logging.error(cex.getMessage());
                if (cex.getPotentialFix() != null) Utils.Logging.error("Potential fix: "+cex.getPotentialFix());
            } catch (Exception e) {
                Utils.Logging.error("Error while running command " + command);
                e.printStackTrace();
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
