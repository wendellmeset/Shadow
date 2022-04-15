/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.DoubleArgumentParser;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.feature.command.coloring.StaticArgumentServer;
import net.shadow.client.feature.command.exception.CommandException;

public class ApplyVel extends Command {

    public ApplyVel() {
        super("ApplyVel", "Apply velocity to your player", "velocity", "vel", "applyVel", "yeet");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {

        return StaticArgumentServer.serveFromStatic(lookingAtArgIndex, ArgumentType.NUMBER, ArgumentType.NUMBER, ArgumentType.NUMBER);
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
        validateArgumentsLength(args, 3, "Provide X, Y and Z velocity");

        DoubleArgumentParser dap = new DoubleArgumentParser();
        Double vx = dap.parse(args[0]);
        Double vy = dap.parse(args[1]);
        Double vz = dap.parse(args[2]);

        ShadowMain.client.player.addVelocity(vx, vy, vz);
    }
}
