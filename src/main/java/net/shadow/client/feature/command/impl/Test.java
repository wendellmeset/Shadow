/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.nbt.NbtGroup;
import net.shadow.client.helper.nbt.NbtList;
import net.shadow.client.helper.nbt.NbtObject;
import net.shadow.client.helper.nbt.NbtProperty;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {

    }
}
