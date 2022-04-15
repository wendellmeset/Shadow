/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.util.math.Direction;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.coloring.ArgumentType;

public class CheckCmd extends Command {
    public CheckCmd() {
        super("CheckCmd", "Check if command blocks are enabled", "checkCmd");
    }

    @Override
    public ArgumentType getArgumentType(String[] args, String lookingAtArg, int lookingAtArgIndex) {
        return null;
    }

    @Override
    public void onExecute(String[] args) {
        message("Checking command blocks");
        ShadowMain.client.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(ShadowMain.client.player.getBlockPos().offset(Direction.DOWN, 1), "/", CommandBlockBlockEntity.Type.AUTO, false, false, false));
    }
}
