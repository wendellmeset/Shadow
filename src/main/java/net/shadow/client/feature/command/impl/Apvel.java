/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.DoubleArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

public class Apvel extends Command {
    private Double vx;
    private Double vy;
    private Double vz;

    public Apvel() {
        super("Apvel", "apply velocity to your character", "velocity", "vel", "apvel");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(x velocity)"};
        } else if (args.length == 2) {
            return new String[]{"(y velocity)"};
        } else if (args.length == 3) {
            return new String[]{"(z velocity)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 3);

        DoubleArgumentParser dap = new DoubleArgumentParser();
        vx = dap.parse(args[0]);
        vy = dap.parse(args[1]);
        vz = dap.parse(args[2]);

        ShadowMain.client.player.addVelocity(vx, vy, vz);
    }
}
