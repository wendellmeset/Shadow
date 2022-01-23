/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.ModuleConfig;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module {

    protected static final MinecraftClient client = CoffeeClientMain.client;
    public final ModuleConfig config;
    public final DoubleSetting keybind;
    private final BooleanSetting debuggerEnabled;
    private final String name;
    private final String description;
    private final ModuleType moduleType;
    private final BooleanSetting toasts;
    private boolean enabled = false;

    public Module(String n, String d, ModuleType type) {
        this.name = n;
        this.description = d;
        this.moduleType = type;
        this.config = new ModuleConfig();
        this.keybind = this.config.create(new DoubleSetting.Builder(-1).name("Keybind").description("The keybind to toggle the module with").min(-1).max(65535).precision(0).get());
        this.keybind.showIf(() -> false);
        this.debuggerEnabled = this.config.create(new BooleanSetting.Builder(false).name("Debugger").description("Shows a lot of funky visuals describing whats going on").get());
        this.toasts = this.config.create(new BooleanSetting.Builder(true).name("Toasts").description("Whether to show enabled / disabled toasts").get());
    }

    protected boolean isDebuggerEnabled() {
        return this.debuggerEnabled.getValue();
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void tick();

    public abstract void enable();

    public abstract void disable();

    public abstract String getContext();

    public abstract void onWorldRender(MatrixStack matrices);

    public abstract void onHudRender();

    public void onHudRenderNoMSAA() {

    }

    public void onWorldRenderNoMSAA(MatrixStack matrices) {

    }

    public void onFastTick() {

    }

    public void onFastTick_NWC() {

    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (toasts.getValue()) {
            Notification.create(1000, "Module toggle", Notification.Type.INFO, (this.enabled ? "§aEn" : "§cDis") + "abled §r" + this.getName());
        }
        if (this.enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

}
