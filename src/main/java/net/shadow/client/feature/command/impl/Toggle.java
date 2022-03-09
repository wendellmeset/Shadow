/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "Toggles a module", "toggle", "t");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(Module::getName).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("ima need the module name");
            return;
        }
        Module m = ModuleRegistry.getByName(String.join(" ", args));
        if (m == null) {
            error("Module not found bruh");
        } else {
            m.toggle();
        }
    }
}
