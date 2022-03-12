package net.shadow.client.feature.command.impl;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.util.math.Direction;

public class CheckCmd extends Command {
    public CheckCmd() {
        super("CheckCmd", "check if command blocks are on", "checkcmd");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        message("Checking command blocks");
        ShadowMain.client.player.networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(ShadowMain.client.player.getBlockPos().offset(Direction.DOWN, 1), "/", CommandBlockBlockEntity.Type.AUTO, false, false, false));
    }
}
