/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.nbt.NbtGroup;
import net.shadow.client.helper.nbt.NbtObject;
import net.shadow.client.helper.nbt.NbtProperty;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }


    @Override
    public void onExecute(String[] args) {
        ItemStack hand = ShadowMain.client.player.getInventory().getMainHandStack();
        hand.setNbt(new NbtGroup(
                new NbtObject("BlockEntityTag",
                        new NbtProperty("x", Double.MAX_VALUE),
                        new NbtProperty("y", 0),
                        new NbtProperty("z", Double.MAX_VALUE))
        ).toCompound());
    }
}
