/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.screen.slot.SlotActionType;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.coloring.ArgumentType;

public class ClearInventory extends Command {
    public ClearInventory() {
        super("ClearInventory", "Clear your inventory the cool way", "clear", "clearInv", "void");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return null;
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        for (int i = 9; i < 45; i++) {
            ShadowMain.client.interactionManager.clickSlot(ShadowMain.client.player.currentScreenHandler.syncId, i, 120, SlotActionType.SWAP, ShadowMain.client.player);
        }
    }
}
