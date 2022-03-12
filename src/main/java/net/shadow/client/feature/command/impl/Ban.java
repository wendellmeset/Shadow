package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;

public class Ban extends Command {
    public Ban() {
        super("Ban", "ban people from joining the server again");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length == 1){
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        String name = Utils.Players.completeName(args[0]);
        int[] player = Utils.Players.decodeUUID(Utils.Players.getUUIDFromName(name));
        if (player == null) {
            error("Invalid Player");
            return;
        }
        ItemStack ban = new ItemStack(Items.ARMOR_STAND, 1);
        error("Created Ban Stand for " + name);
        try {
            ban.setNbt(StringNbtReader.parse("{EntityTag:{UUID:[I;" + player[0] + "," + player[1] + "," + player[2] + "," + player[3] + "],ArmorItems:[{},{},{},{id:\"minecraft:player_head\",Count:1b,tag:{SkullOwner:\"" + name + "\"}}]}}"));
        } catch (Exception ignored) {
        }
        ban.setCustomName(Text.of(name));
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, ban));
    }
}
