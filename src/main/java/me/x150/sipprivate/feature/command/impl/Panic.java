/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;

import java.util.ArrayList;
import java.util.List;

public class Panic extends Command {

    final List<Module> stored = new ArrayList<>();

    public Panic() {
        super("Panic", "Turns off all modules in case you get caught", "panic", "p", "disableall");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"hard", "restore"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            stored.clear();
            message("Disabling all non-render modules");
            message("Specify \"hard\" to disable all modules and wipe chat");
            message("Specify \"restore\" to restore all enabled modules before the panic");
            for (Module module : ModuleRegistry.getModules()) {
                if (module.getModuleType() != ModuleType.RENDER && module.isEnabled()) {
                    stored.add(module);
                    module.setEnabled(false);
                }
            }
        } else if (args[0].equalsIgnoreCase("hard")) {
            stored.clear();
            for (Module module : ModuleRegistry.getModules()) {
                if (module.isEnabled()) {
                    stored.add(module);
                    module.setEnabled(false);
                }
            }
            CoffeeClientMain.client.inGameHud.getChatHud().clear(true);
        } else if (args[0].equalsIgnoreCase("restore")) {
            if (stored.size() == 0) {
                error("The stored module list is empty");
            } else {
                for (Module module : stored) {
                    if (!module.isEnabled()) {
                        module.setEnabled(true);
                    }
                }
            }
            stored.clear();
        }
    }
}
