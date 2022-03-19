/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.argument;

import net.shadow.client.feature.command.exception.CommandException;

public class StringArgumentParser implements ArgumentParser<String> {
    @Override
    public String parse(String argument) throws CommandException {
        return argument;
    }
}
