/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Pre extends Command {
    public Pre() {
        super("SpawnData", "set pre-spawn conditions for spawn eggs", "prespawn", "spawndata");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return new String[]{"position", "velocity", "cursor"};
        }
        if(args.length < 1){
            if(args[0].equals("position") || args[0].equals("velocity")){
                if(args.length == 2){
                    return new String[]{"x"};
                }
                if(args.length == 2){
                    return new String[]{"y"};
                }
                if(args.length == 2){
                    return new String[]{"z"};
                }
            }
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args[0].equalsIgnoreCase("position")) {
            try {
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                NbtCompound tag = StringNbtReader.parse("{EntityTag:{Pos:[" + args[1] + ".5," + args[2] + ".0," + args[3] + ".5," + "]}}");
                stack.getNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Spawning Position");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("velocity")) {
            try {
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                NbtCompound tag = StringNbtReader.parse("{EntityTag:{Motion:[" + args[1] + ".0," + args[2] + ".0," + args[3] + ".0," + "]}}");
                stack.getNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Spawning Motion");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("cursor")) {
            try {
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                Vec3d se = Objects.requireNonNull(ShadowMain.client.player).raycast(255, ShadowMain.client.getTickDelta(), true).getPos();
                NbtCompound tag = StringNbtReader.parse("{EntityTag:{Pos:[" + se.x + "," + se.y + "," + se.z + "," + "]}}");
                stack.getNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Spawning Position");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            error("Please use the format >prespawn <position/velocity/cursor> <x> <y> <z>");
        }
    }
}
