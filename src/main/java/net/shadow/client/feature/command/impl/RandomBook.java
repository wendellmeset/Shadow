/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomBook extends Command {

    public RandomBook() {
        super("RandomBook", "write random books", "RandomBook", "rbook");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"Ascii", "Raw", "Unicode"};
        }
        if(args.length == 2){
            return new String[]{"(pages)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        try {
            validateArgumentsLength(args, 2);
        } catch (CommandException e) {
            error("Please use >RandomBook (mode) (pages)");
        }
        int size;
        try {
            size = Integer.parseInt(args[1]);
        } catch (Exception e) {
            error("Please use >RandomBook (mode) (pages)");
            return;
        }
        switch(args[0].toLowerCase()){
            case "raw" -> {
                List<String> title = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    StringBuilder page2 = new StringBuilder();
                    page2.append(String.valueOf((char) 2048).repeat(266));
                    title.add(page2.toString());
                }

                Optional<String> pages = Optional.of("Raw");
                client.player.networkHandler.sendPacket(new BookUpdateC2SPacket(client.player.getInventory().selectedSlot, title, pages));
            }

            case "ascii" -> {
                Random r = new Random();
                List<String> title3 = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    StringBuilder page = new StringBuilder();
                    for (int j = 0; j < 266; j++) {
                        page.append((char) r.nextInt(25) + 97);
                    }
                    title3.add(page.toString());
                }


                Optional<String> pages3 = Optional.of("Ascii");
                client.player.networkHandler.sendPacket(new BookUpdateC2SPacket(client.player.getInventory().selectedSlot, title3, pages3));
            }

            case "unicode" -> {
                IntStream chars = new Random().ints(0, 0x10FFFF + 1);
                String text = chars.limit(210 * Math.round(size)).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
                List<String> title2 = new ArrayList<>();
                Optional<String> pages2 = Optional.of("Unicode");

                for (int t = 0; t < size; t++) {
                    title2.add(text.substring(t * 210, (t + 1) * 210));
                }

                client.player.networkHandler.sendPacket(new BookUpdateC2SPacket(client.player.getInventory().selectedSlot, title2, pages2));
            }
        }
    }
}
