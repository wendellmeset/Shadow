/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import java.util.Random;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class FloodLP extends Command {
    public FloodLP() {
        super("FloodLuckperms", "flood luckperm groups", "floodlp", "lpflood", "floodluckperms", "luckpermsflood");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 1) {
            error("use >floodlp <amount>");
            return;
        }
        int pp = 0;
        try {
            pp = Integer.parseInt(args[0]);
        } catch (Exception e) {
            error("use >floodlp <amount>");
        }
        Random r = new Random();
        for (int i = 0; i < pp; i++) {
            ShadowMain.client.player.sendChatMessage("/lp creategroup " + i + r.nextInt(100));
        }
    }
}
