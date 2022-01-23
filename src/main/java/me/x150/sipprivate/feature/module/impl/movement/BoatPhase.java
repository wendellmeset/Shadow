/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatPhase extends Module {

    public BoatPhase() {
        super("BoatPhase", "Allows you to go through blocks, when in a boat which sand is falling on", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        Utils.Logging.message("To use BoatPhase, go into a boat, move it all the way towards a wall and drop sand on the boat with you in it");
    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        if (!(CoffeeClientMain.client.player.getVehicle() instanceof BoatEntity)) {
            Notification.create(5000, "Boat phase", true, Notification.Type.INFO, "sir you need a boat");
            setEnabled(false);
            return;
        }
        CoffeeClientMain.client.player.getVehicle().noClip = true;
        CoffeeClientMain.client.player.getVehicle().setNoGravity(true);
        CoffeeClientMain.client.player.noClip = true;
    }

    @Override
    public void onHudRender() {

    }
}

