/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.argument;

import net.shadow.client.feature.command.exception.CommandException;

public class DoubleArgumentParser implements ArgumentParser<Double> {
    @Override
    public Double parse(String argument) throws CommandException {
        try {
            return Double.parseDouble(argument);
        } catch (Exception e) {
            throw new CommandException("Invalid argument \"" + argument + "\": Expected a double", "Provide a valid number");
        }
    }
}
