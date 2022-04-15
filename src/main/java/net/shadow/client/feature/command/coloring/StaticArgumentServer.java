/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.coloring;

public class StaticArgumentServer {
    public static ArgumentType serveFromStatic(int index, ArgumentType ...types) {
        if (index >= types.length) return null;
        return types[index];
    }
}
