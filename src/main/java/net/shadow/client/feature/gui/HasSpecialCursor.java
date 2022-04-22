/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui;

public interface HasSpecialCursor {
    long getCursor();

    boolean shouldApplyCustomCursor();
}
