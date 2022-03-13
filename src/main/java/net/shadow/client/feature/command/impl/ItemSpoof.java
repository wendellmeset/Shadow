/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class ItemSpoof extends Command {
    public ItemSpoof() {
        super("ItemSpoof", "spoof give yourself items", "ispoof", "itemspoof", "ghostitem", "ghostgive");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return Registry.ITEM.stream().toList().toArray(String[]::new);
        }else if(args.length ==2){
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 2) {
            error("Please use the format >ghostitem <item> <amount>");
            return;
        }

        int stacksize = Integer.parseInt(args[1]);
        Item item = Registry.ITEM.get(new Identifier(args[0]));
        ItemStack stack = new ItemStack(item, stacksize);
        ShadowMain.client.player.getInventory().armor.set(3, stack);
    }
}
