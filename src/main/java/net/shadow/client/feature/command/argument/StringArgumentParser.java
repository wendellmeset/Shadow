/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.argument;

public class StringArgumentParser implements ArgumentParser<String> {
    @Override
    public String parse(String argument) {
        return argument;
    }
}
