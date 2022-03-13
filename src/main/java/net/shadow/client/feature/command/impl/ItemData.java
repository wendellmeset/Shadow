/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import java.util.Objects;

import net.minecraft.entity.player.PlayerInventory;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

public class ItemData extends Command {
    public ItemData() {
        super("ItemData", "get items from other ppl", "itemdata", "idata");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        if(args.length == 2){
            return new String[]{"hand", "offhand", "head", "chest", "legs", "feet"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length != 2) {
            message("Please use the format >itemdata <player> <slot>");
            return;
        }


        String playerh = Utils.Players.completeName(args[0]);
        AbstractClientPlayerEntity player = getPlayer(playerh);
        ItemStack item = getItem(player, args[1]);
        if (item == null || player == null) return;
        if (ShadowMain.client.player.getAbilities().creativeMode) {
            giveItem(item);
        } else {
            NbtCompound tag = item.getNbt();
            String nbt = tag == null ? "" : tag.asString();
            ShadowMain.client.keyboard.setClipboard(nbt);
            message("You were not in creative mode, so the nbt was copied to your clipboard!");
        }


        message("Item copied.");
    }

    private ItemStack getItem(AbstractClientPlayerEntity player, String slot) {
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

    private AbstractClientPlayerEntity getPlayer(String name) {
        for (AbstractClientPlayerEntity player : ShadowMain.client.world.getPlayers()) {
            if (!player.getEntityName().equalsIgnoreCase(name))
                continue;

            return player;
        }

        {
            message("Player could not be found!");
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
