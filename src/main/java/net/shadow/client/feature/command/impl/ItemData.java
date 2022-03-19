/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.PlayerFromNameArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.helper.util.Utils;

import java.util.Objects;

public class ItemData extends Command {
    public ItemData() {
        super("ItemData", "get items from other ppl", "itemdata", "idata");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        if (args.length == 2) {
            return new String[]{"hand", "offhand", "head", "chest", "legs", "feet"};
        }
        if (args.length == 3) {
            return new String[] {"--onlyShow"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 2);

        boolean onlyShow = args.length > 2 && args[2].equalsIgnoreCase("--onlyShow");
        PlayerEntity player = new PlayerFromNameArgumentParser(true).parse(args[0]);
        ItemStack item = getItem(player, args[1]);
        if (item == null) return;
        if (ShadowMain.client.interactionManager.hasCreativeInventory() && !onlyShow) {
            giveItem(item);
            message("Item copied.");
        } else {
            NbtCompound tag = item.getNbt();
            String nbt = tag == null ? "" : tag.asString();
            ShadowMain.client.keyboard.setClipboard(nbt);
            message("Nbt copied.");
        }

    }

    private ItemStack getItem(PlayerEntity player, String slot) {
        switch (slot.toLowerCase()) {
            case "hand":
                return player.getInventory().getMainHandStack();

            case "offhand":
                return player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT);

            case "head":
                return player.getInventory().getArmorStack(3);

            case "chest":
                return player.getInventory().getArmorStack(2);

            case "legs":
                return player.getInventory().getArmorStack(1);

            case "feet":
                return player.getInventory().getArmorStack(0);

            default:
                message("Please use the format >itemdata <player> <slot>");
                return null;
        }
    }

    private void giveItem(ItemStack stack) {
        int slot = ShadowMain.client.player.getInventory().getEmptySlot();
        if (slot < 0) {
            message("Please clear a slot in your hotbar");
            return;
        }

        if (slot < 9)
            slot += 36;

        CreativeInventoryActionC2SPacket packet =
                new CreativeInventoryActionC2SPacket(slot, stack);
        ShadowMain.client.player.networkHandler.sendPacket(packet);
    }
}
