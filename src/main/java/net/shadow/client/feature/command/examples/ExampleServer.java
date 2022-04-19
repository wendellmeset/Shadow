/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.examples;

import net.shadow.client.feature.command.Command;

public class ExampleServer {
    public static Command.ExamplesEntry getPlayerNames() {
        return new Command.ExamplesEntry("Notch", "Herobrine", "Player123");
    }
}
