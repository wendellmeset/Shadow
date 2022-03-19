/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import java.util.Arrays;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class FSpam extends Command {
    public FSpam() {
        super("FSpam", "fast spammer", "fspam", "fastspam", "quickspam");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return new String[]{"(amount)"};
        }
        if(args.length > 1){
            return new String[]{"(message)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        int amount = 0;
        try{
            amount = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            error("Not a valid integer!");
            return;
        }
        for(int i = 0; i < amount; i++){
            ShadowMain.client.player.sendChatMessage(String.join("", Arrays.copyOfRange(args, 1, args.length)));
        }
    }
}
