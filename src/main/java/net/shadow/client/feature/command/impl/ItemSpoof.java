/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import lombok.val;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class ItemSpoof extends Command {
    public ItemSpoof() {
        super("ItemSpoof", "spoof give yourself items", "ispoof", "itemspoof", "ghostitem", "ghostgive");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Registry.ITEM.stream().map(p -> Registry.ITEM.getId(p).toString()).toList().toArray(String[]::new);
        } else if (args.length == 2) {
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 2);

        IntegerArgumentParser integerArgumentParser = new IntegerArgumentParser();
        int amount = integerArgumentParser.parse(args[1]);
        Identifier i = Identifier.tryParse(args[0]);
        if (i == null) throw new CommandException("Invalid name \""+args[0]+"\"", "Provide valid item identifier");
        Item item = Registry.ITEM.get(i);
        ItemStack stack = new ItemStack(item, amount);
        ShadowMain.client.player.getInventory().armor.set(3, stack);
    }
}
