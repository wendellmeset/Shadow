package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Arrays;

public class Inject extends Command {
    public Inject() {
        super("Inject", "Injects a chunk of nbt into the target item", "inject", "inj", "addNbt");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{
                    "set", "clear", "add"
            };
        } else if (args.length > 1) {
            String action = args[0].toLowerCase();
            if (action.equalsIgnoreCase("set") || action.equalsIgnoreCase("add")) return new String[]{"(nbt)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("Syntax: inj [set, clear, add] (nbt)");
            return;
        }
        if (!ShadowMain.client.interactionManager.hasCreativeInventory()) {
            error("no creative mode? :megamind:");
            return;
        }
        ItemStack is = ShadowMain.client.player.getInventory().getMainHandStack();
        if (is.isEmpty()) {
            error("hold and item shithead");
            return;
        }
        String action = args[0].toLowerCase();
        switch (action) {
            case "set" -> {
                if (args.length < 2) {
                    error("Syntax: inj set (nbt)");
                    return;
                }
                String nString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                try {
                    NbtCompound ncNew = StringNbtReader.parse(nString);
                    is.setNbt(ncNew);
                    success("Item modified");
                } catch (Exception e) {
                    error(e.getMessage());
                }
            }
            case "clear" -> {
                is.setNbt(new NbtCompound());
                success("Item cleared");
            }
            case "add" -> {
                if (args.length < 2) {
                    error("Syntax: inj add (nbt)");
                    return;
                }
                String nString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                NbtCompound old = is.getOrCreateNbt();
                try {
                    NbtCompound ncNew = StringNbtReader.parse(nString);
                    old.copyFrom(ncNew);
                    success("Item modified");
                } catch (Exception e) {
                    error(e.getMessage());
                }
            }
            default -> error("Invalid subcommand. Choose set, clear or add.");
        }

    }
}
