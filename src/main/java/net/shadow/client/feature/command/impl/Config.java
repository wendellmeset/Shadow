/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.feature.config.SettingBase;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;

import java.util.Arrays;
import java.util.Objects;

public class Config extends Command {

    public Config() {
        super("Config", "Changes configuration of a module", "config", "conf");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(mod -> mod.getName().replaceAll(" ", "-")).toList().toArray(String[]::new);
        } else if (args.length == 2 && ModuleRegistry.getByName(args[0]) != null) {
            return Objects.requireNonNull(ModuleRegistry.getByName(args[0].replaceAll("-", " "))).config.getSettings().stream().map(SettingBase::getName).toList().toArray(String[]::new);
        } else if (args.length == 3) {
            return new String[]{"(New value)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
//        validateArgumentsLength(args, 1);
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
