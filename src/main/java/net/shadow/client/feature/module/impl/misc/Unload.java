/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.ModuleType;

public class Unload extends Module {
    public static boolean loaded = true;

    public Unload() {
        super("Unload", "unload the client for the most part", ModuleType.MISC);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        loaded = false;
        for(Module m : ModuleRegistry.getModules()){
            m.setEnabled(false);
        }
        Notification.create(1000, "Unload", Notification.Type.SUCCESS, "Client Unloaded!");
        this.setEnabled(false);
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

    }

    @Override
    public void onHudRender() {

    }
}
