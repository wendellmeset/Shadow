/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.addon;

import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.module.AddonModule;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public abstract class Addon {
    private final AtomicBoolean isEnabled = new AtomicBoolean(false);

    public abstract String getName();

    public abstract String getDescription();

    public abstract BufferedImage getIcon();

    public abstract String[] getAuthors();

    public abstract List<AddonModule> getAdditionalModules();

    public abstract List<Command> getAdditionalCommands();

    public final void onEnable() {
        isEnabled.set(true);
        enabled();
    }

    public final void onDisable() {
        isEnabled.set(false);
        disabled();
    }

    public final boolean isEnabled() {
        return isEnabled.get();
    }

    public abstract void enabled();

    public abstract void disabled();

    public abstract void reloaded();
}
