/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.module.AddonModule;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (Module module : ModuleRegistry.getModules()) {
            if (module instanceof AddonModule) continue;
            String cname = module.getClass().getSimpleName();
            sb.append(String.format("registerModule(%s.class);", cname)).append("\n");
        }
        try {
            Files.writeString(new File("bruh.txt").toPath(), sb.toString(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
