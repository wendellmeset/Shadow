/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Arrays;

public class Open extends Command {
    private static final ItemStack[] ITEMS = new ItemStack[27];

    public Open() {
        super("Open", "Open contianers", "open");
    }

    private static void getItemsInContainerItem(ItemStack itemStack) {
        Arrays.fill(Open.ITEMS, ItemStack.EMPTY);
        NbtCompound nbt = itemStack.getNbt();

        if (nbt != null && nbt.contains("ShadowItemTag")) {
            NbtCompound nbt2 = nbt.getCompound("ShadowItemTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    ShadowMain.client.player.networkHandler.sendPacket(
                            new CreativeInventoryActionC2SPacket(nbt3.getCompound(i).getByte("Slot") + 9, ItemStack.fromNbt(nbt3.getCompound(i))));
                }
            }
        }
        if (nbt != null && nbt.contains("BlockEntityTag")) {
            NbtCompound nbt2 = nbt.getCompound("BlockEntityTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    ShadowMain.client.player.networkHandler.sendPacket(
                            new CreativeInventoryActionC2SPacket(nbt3.getCompound(i).getByte("Slot") + 9, ItemStack.fromNbt(nbt3.getCompound(i))));
                }
            }
        }
    }

    @Override
    public void onExecute(String[] args) {
        success("Done!");
        getItemsInContainerItem(ShadowMain.client.player.getMainHandStack());
    }

}
