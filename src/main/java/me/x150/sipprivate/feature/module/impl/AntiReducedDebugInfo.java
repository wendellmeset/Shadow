/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;

public class AntiReducedDebugInfo extends Module {

    public AntiReducedDebugInfo() {
        super("AntiRDI", "Stops the \"reduced debug info\" gamerule from taking effect", ModuleType.EXPLOIT);
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        boolean origDebugInfoReduce = Objects.requireNonNull(CoffeeClientMain.client.player).hasReducedDebugInfo() || CoffeeClientMain.client.options.reducedDebugInfo;
        return origDebugInfoReduce ? "Active!" : null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

