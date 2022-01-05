/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import net.minecraft.text.Text;

import java.util.Objects;

public class Rename extends Command {

    public Rename() {
        super("Rename", "Renames an item (requires creative)", "rename", "rn", "name");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(new item name)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("I need a new name dude");
            error("example: rename &c&lthe &afunny");
            return;
        }
        if (Objects.requireNonNull(CoffeeClientMain.client.player).getInventory().getMainHandStack().isEmpty()) {
            error("idk if you're holding anything");
            return;
        }
        CoffeeClientMain.client.player.getInventory().getMainHandStack().setCustomName(Text.of("§r" + String.join(" ", args).replaceAll("&", "§")));
    }
}
