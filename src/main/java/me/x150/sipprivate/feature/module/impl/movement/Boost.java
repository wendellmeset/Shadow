/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.movement;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class Boost extends Module {

    //    final SliderValue strength = (SliderValue) this.config.create("Strength", 3, 0.1, 10, 1).description("The strength to boost you by");
//    final MultiValue  mode     = (MultiValue) this.config.create("Mode", "add", "add", "overwrite").description("The mode of which to affect your velocity by");
    final DoubleSetting strength = this.config.create(new DoubleSetting.Builder(3)
            .name("Strength")
            .description("How much to boost you with")
            .min(0.1)
            .max(10)
            .precision(1)
            .get());
    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Add)
            .name("Mode")
            .description("How to boost you")
            .get());

    public Boost() {
        super("Boost", "Boosts you into the air", ModuleType.MOVEMENT);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
            return;
        }
        setEnabled(false);
        Vec3d newVelocity = CoffeeClientMain.client.player.getRotationVector().multiply(strength.getValue());
        if (this.mode.getValue() == Mode.Add) {
            CoffeeClientMain.client.player.addVelocity(newVelocity.x, newVelocity.y, newVelocity.z);
        } else {
            CoffeeClientMain.client.player.setVelocity(newVelocity);
        }
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

    public enum Mode {
        Add, Overwrite
    }
}
