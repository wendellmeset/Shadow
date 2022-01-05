/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.feature.config.SettingBase;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config extends Command {

    public Config() {
        super("Config", "Changes configuration of a module", "config", "conf");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(Module::getName).collect(Collectors.toList()).toArray(String[]::new);
        } else if (args.length == 2 && ModuleRegistry.getByName(args[0]) != null) {
            return Objects.requireNonNull(ModuleRegistry.getByName(args[0])).config.getSettings().stream().map(SettingBase::getName).collect(Collectors.toList()).toArray(String[]::new);
        } else if (args.length == 3) {
            return new String[]{"(New value)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            message("Syntax: .config (module) <key> <value>");
            message("For a module or key with spaces, use - as a separator");
            message("Example: \".config block-spammer times-per-tick 11\" to set the \"times per tick\" property to 11");
            return;
        }
        Module target = ModuleRegistry.getByName(args[0].replaceAll("-", " "));
        if (target == null) {
            error("Module not found");
            return;
        }
        if (args.length == 1) {
            for (SettingBase<?> dynamicValue : target.config.getSettings()) {
                message(dynamicValue.getName() + " = " + dynamicValue.getValue().toString());
            }
        } else if (args.length == 2) {
            SettingBase<?> val = target.config.get(args[1].replaceAll("-", " "));
            if (val == null) {
                error("Key not found");
                return;
            }
            message(val.getName() + " = " + val.getValue().toString());
        } else if (args.length == 3) {
            SettingBase<?> val = target.config.get(args[1].replaceAll("-", " "));
            if (val == null) {
                error("Key not found");
                return;
            }
            val.accept(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
        }
    }
}
