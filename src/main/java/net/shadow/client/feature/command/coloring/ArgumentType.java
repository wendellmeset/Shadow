/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.coloring;

import java.awt.Color;

public enum ArgumentType {
    STRING(new Color(0x55FF55)),
    NUMBER(new Color(0x009DFF)),
    PLAYER(new Color(0xFF9900));
    final Color color;

    ArgumentType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
