/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

public class Damage extends Command {
    public Damage() {
        super("Damage", "kys", "damage", "kms");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 0) {
            return new String[]{"(integer)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length != 1) {
            error("Please use the format >damage <amount>");
            return;
        }

        if (ShadowMain.client.player.getAbilities().creativeMode) {
            error("you cannot damage in creative mode");
            return;
        }

        int amount = parseAmount(args[0]);
        if (amount == 0) return;
        applyDamage(amount);
        message("Applied Damage");
    }

    private int parseAmount(String dmgString) {
        try {
            return Integer.parseInt(dmgString);
        } catch (Exception e) {
            {
                error("Not a number!");
                return 0;
            }
        }
    }


    private void applyDamage(int amount) {
        Vec3d pos = ShadowMain.client.player.getPos();

        for (int i = 0; i < 80; i++) {
            sendPosition(pos.x, pos.y + amount + 2.1, pos.z, false);
            sendPosition(pos.x, pos.y + 0.05, pos.z, false);
        }

        sendPosition(pos.x, pos.y, pos.z, true);
    }

    private void sendPosition(double x, double y, double z, boolean onGround) {
        ShadowMain.client.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }
}
