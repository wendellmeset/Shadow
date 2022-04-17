/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

public class DauntedAutoClaim extends Module {

    public DauntedAutoClaim() {
        super("DauntedAutoClaim", "Daunted auto claim", ModuleType.MISC);
    }

    @Override
    public void tick() {
        for(int i = 0; i < 8; i++){
            ItemStack selected = client.player.getInventory().getStack(i);
            if(selected.getItem().equals(Items.PAPER)){
                int before = client.player.getInventory().selectedSlot;
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
                client.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(before));
            }
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
