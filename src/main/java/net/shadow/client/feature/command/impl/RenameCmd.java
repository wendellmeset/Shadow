/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class RenameCmd extends Command {
    public RenameCmd() {
        super("Rename", "rename items", "rename", "rname");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (!ShadowMain.client.player.getAbilities().creativeMode) {
            error("you must be in creative mode");
            return;
        }

        if (args.length == 0) {
            error("Please use the format >rename <name>");
            return;
        }

        StringBuilder message = new StringBuilder(args[0]);
        for (int i = 1; i < args.length; i++)
            message.append(" ").append(args[i]);

        message = new StringBuilder(message.toString().replace("&", "\u00a7").replace("\u00a7\u00a7", "&"));
        ItemStack item = ShadowMain.client.player.getInventory().getMainHandStack();

        if (item == null) {
            message("You must hold an item");
            return;
        }

        item.setCustomName(new LiteralText(message.toString()));
        message("Renamed item to \"" + message + "\u00a7r\".");
    }
}
