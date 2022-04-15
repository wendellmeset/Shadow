/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.feature.command.coloring.StaticArgumentServer;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "Toggles a module", "toggle", "t");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return StaticArgumentServer.serveFromStatic(lookingAtArgIndex, ArgumentType.STRING);
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(Module::getName).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Provide module name");
        Module m = ModuleRegistry.getByName(String.join(" ", args));
        if (m == null) {
            throw new CommandException("Module not found", "Specify a module name that exists");
        } else {
            m.toggle();
        }
    }
}
