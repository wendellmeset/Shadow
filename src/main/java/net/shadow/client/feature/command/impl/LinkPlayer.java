/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;

public class LinkPlayer extends Command {
    public LinkPlayer() {
        super("LinkPlayer", "link a wolf to a player", "linkplayer", "lplayer");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(AbstractClientPlayerEntity::getGameProfile).map(GameProfile::getName).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (!ShadowMain.client.player.getAbilities().creativeMode) {
            error("You must be in creative mode");
            return;
        }
        if (args.length < 1) {
            error("Incorrect Arguments, use >linkplayer <player>");
            return;
        }
        String player = Utils.Players.completeName(args[0]);
        if (player.equals("none")) {
            error("that player does not exist");
            return;
        }
        int[] ub = Utils.Players.decodeUUID(Utils.Players.getUUIDFromName(player));
        if (ub == null) {
            error("that player does not exist");
            return;
        }
        NbtCompound tag = new NbtCompound();
        try {
            tag = StringNbtReader.parse("{EntityTag:{CustomNameVisible:0b,Owner:[I;" + ub[0] + "," + ub[1] + "," + ub[2] + "," + ub[3] + "],Sitting:1b}}");
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        Item item = Registry.ITEM.get(new Identifier("wolf_spawn_egg"));
        ItemStack handitem = ShadowMain.client.player.getMainHandStack();
        ItemStack stack = new ItemStack(item, 1);
        stack.setNbt(tag);
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) ShadowMain.client.crosshairTarget));
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, handitem));
        message("Spawned linked wolf for " + player);
    }
}
