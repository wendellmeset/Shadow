package net.shadow.client.feature.command.impl;

import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtString;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Author extends Command {
    public Author() {
        super("Author", "set the author of a book in creative", "author");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if(args.length > 0){
            return new String[]{"(new author)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("Please use the format >author <author>");
            return;
        }

        if (!ShadowMain.client.player.getAbilities().creativeMode) {
            error("You must be in creative mode to do this!");
            return;
        }

        ItemStack heldItem = ShadowMain.client.player.getInventory().getMainHandStack();
        int heldItemID = Item.getRawId(heldItem.getItem());
        int writtenBookID = Item.getRawId(Items.WRITTEN_BOOK);

        if (heldItemID != writtenBookID) {
            error("You must hole a written book");
            return;
        }
        String author = String.join(" ", args);
        heldItem.setSubNbt("author", NbtString.of(author));
    }
}
