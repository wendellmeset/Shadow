/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.argument.DoubleArgumentParser;
import net.shadow.client.feature.command.exception.CommandException;
import net.shadow.client.helper.nbt.NbtGroup;
import net.shadow.client.helper.nbt.NbtList;
import net.shadow.client.helper.nbt.NbtObject;
import net.shadow.client.helper.nbt.NbtProperty;

import java.util.Objects;

public class SpawnData extends Command {
    public SpawnData() {
        super("SpawnData", "Set pre-spawn conditions for spawn eggs", "preSpawn", "spawnData");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"position", "velocity", "cursor"};
        }
        if (args.length > 1) {
            if (args[0].equals("position") || args[0].equals("velocity")) {
                return switch (args.length) {
                    case 2 -> new String[]{"x"};
                    case 3 -> new String[]{"y"};
                    case 4 -> new String[]{"z"};
                    default -> new String[0];
                };
            }
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        validateArgumentsLength(args, 1, "Provide data point");
        DoubleArgumentParser dap = new DoubleArgumentParser();
        switch (args[0].toLowerCase()) {
            case "position" -> {
                validateArgumentsLength(args, 4, "Provide X, Y and Z coordinates");
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());

                NbtGroup ng = new NbtGroup(
                        new NbtObject("EntityTag",
                                new NbtList("Pos",
                                        new NbtProperty(dap.parse(args[1])),
                                        new NbtProperty(dap.parse(args[2])),
                                        new NbtProperty(dap.parse(args[3]))
                                )
                        )
                );
                NbtCompound tag = ng.toCompound();
                stack.getOrCreateNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Spawning Position");
            }
            case "velocity" -> {
                validateArgumentsLength(args, 4, "Provide X, Y and Z velocity");
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                NbtGroup ng = new NbtGroup(
                        new NbtObject("EntityTag",
                                new NbtList("Motion",
                                        new NbtProperty(dap.parse(args[1])),
                                        new NbtProperty(dap.parse(args[2])),
                                        new NbtProperty(dap.parse(args[3]))
                                )
                        )
                );
                NbtCompound tag = ng.toCompound();
                stack.getOrCreateNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Velocity");
            }
            case "cursor" -> {
                ItemStack stack = ShadowMain.client.player.getInventory().getMainHandStack();
                if (!stack.hasNbt())
                    stack.setNbt(new NbtCompound());
                Vec3d se = Objects.requireNonNull(ShadowMain.client.player).raycast(255, ShadowMain.client.getTickDelta(), true).getPos();
                NbtGroup ng = new NbtGroup(
                        new NbtObject("EntityTag",
                                new NbtList("Pos",
                                        new NbtProperty(se.x),
                                        new NbtProperty(se.y),
                                        new NbtProperty(se.z)
                                )
                        )
                );
                NbtCompound tag = ng.toCompound();
                stack.getOrCreateNbt().copyFrom(tag);
                ShadowMain.client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + ShadowMain.client.player.getInventory().selectedSlot, stack));
                message("Changed Spawning Position");
            }
            default -> error("Please use the format >prespawn <position/velocity/cursor> <x> <y> <z>");
        }
    }
}
