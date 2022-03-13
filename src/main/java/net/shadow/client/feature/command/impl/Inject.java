/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Arrays;

public class Inject extends Command {
    public Inject() {
        super("Inject", "Injects a chunk of nbt into the target item", "inject", "inj", "addNbt");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        ItemStack is = ShadowMain.client.player.getMainHandStack();
        if (args.length < 2) {
            error("Syntax: inj add (nbt)");
            return;
        }
        String nString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        NbtCompound old = is.getOrCreateNbt();
        try {
            NbtCompound ncNew = StringNbtReader.parse(nString);
            old.copyFrom(ncNew);
            success("Item modified");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }
}
