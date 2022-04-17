/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.coloring.ArgumentType;
import net.shadow.client.helper.nbt.NbtGroup;
import net.shadow.client.helper.nbt.NbtObject;
import net.shadow.client.helper.nbt.NbtProperty;
import net.shadow.client.helper.util.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return null;
    }

    @Override
    public void onExecute(String[] args) {
        Utils.TickManager.runOnNextRender(() -> {
            GenericContainerScreenHandler gcsh = new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, client.player.currentScreenHandler.syncId, client.player.getInventory(), new SimpleInventory(46), 5);
            client.setScreen(new GenericContainerScreen(gcsh, client.player.getInventory(), Text.of("REAL")));
        });
    }
}
