/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class Fireball extends Command {
    public Fireball() {
        super("Fireball", "generate fireballs", "fireball");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(power)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);

        int fireballpower = new IntegerArgumentParser().parse(args[0]);
        Item item = Registry.ITEM.get(new Identifier("blaze_spawn_egg"));
        ItemStack stack = new ItemStack(item, 1);
        try {
            stack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Fireball\",\"color\":\"dark_gray\",\"italic\":false}',Lore:['{\"text\":\"Fireball of power " + fireballpower + "\",\"color\":\"gray\",\"italic\":false}']},EntityTag:{id:\"minecraft:fireball\",ExplosionPower:" + fireballpower + ",direction:[0.0,-1.0,0.0],power:[0.0,-1.0,0.0]}}"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
    }
}
