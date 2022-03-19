/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.network.ClientPlayerEntity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Vclip extends Command {
    public Vclip() {
        super("Vclip", "veritcal clip", "vclip");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length != 1) {
            error("Please use the format >vclip <amount>");
            return;
        }

        try {
            ClientPlayerEntity player = ShadowMain.client.player;
            player.updatePosition(player.getX(),
                    player.getY() + Double.parseDouble(args[0]), player.getZ());
        } catch (NumberFormatException e) {
            error("Please use the format >vclip <amount>");
        }
    }
}
