/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.PlayerFromNameArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;

import java.util.Arrays;
import java.util.Objects;

public class FakeItem extends Command {
    public FakeItem() {
        super("FakeItem", "Fakes a person holding a specific item", "fakeItem");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Objects.requireNonNull(ShadowMain.client.world).getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).toList().toArray(String[]::new);
        } else if (args.length == 2) {
            return new String[]{"hand", "custom:(item id) [item nbt]"};
        } else if (args.length == 3 && args[1].toLowerCase().startsWith("custom:")) {
            return new String[]{"(item nbt)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 2, "Provide player and source");
        PlayerEntity le = new PlayerFromNameArgumentParser(true).parse(args[0]);
        if (args[1].equalsIgnoreCase("hand")) {
            ItemStack main = Objects.requireNonNull(ShadowMain.client.player).getMainHandStack().copy();
            if (main.isEmpty()) {
                error("You're not holding anything");
                return;
            }
            le.equipStack(EquipmentSlot.MAINHAND, main);
        } else if (args[1].toLowerCase().startsWith("custom:")) {
            String id = args[1].substring("custom:".length());
            Identifier idParsed = Identifier.tryParse(id);
            if (idParsed == null) {
                error("Invalid item");
                return;
            }

            if (!Registry.ITEM.containsId(idParsed)) {
                error("Item not found");
                return;
            }
            Item item = Registry.ITEM.get(idParsed);
            ItemStack stack = new ItemStack(item);
            if (args.length > 2) { // we got additional nbt
                try {
                    stack.setNbt(StringNbtReader.parse(String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                } catch (CommandSyntaxException e) {
                    error("Invalid NBT: " + e.getContext());
                    return;
                }
            }
            le.equipStack(EquipmentSlot.MAINHAND, stack);
        }
        success("Faked item until manual update by the entity");
    }
}
