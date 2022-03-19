/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.entity.Entity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.IntegerArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class EVclip extends Command {
    public EVclip() {
        super("EVclip", "entity vertical clip", "evc", "evclip", "entityvclip");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(amount)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1);

        if (!ShadowMain.client.player.hasVehicle()) {
            error("Player is not riding an Entity");
            return;
        }

        int up = new IntegerArgumentParser().parse(args[0]);

        Entity vehicle = ShadowMain.client.player.getVehicle();
        vehicle.updatePosition(vehicle.getX(), vehicle.getY() + up, vehicle.getZ());
        message("Teleported entity");
    }
}
