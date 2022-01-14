/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ClickNuke extends Module {

    //    final SliderValue  rangeX  = (SliderValue) this.config.create("Range X", 5, 1, 10, 0).description("How big the affected radius should be in the X dimension");
//    final SliderValue  rangeZ  = (SliderValue) this.config.create("Range Z", 5, 1, 10, 0).description("How big the affected radius should be in the Z dimension");
//    final SliderValue  rangeY  = (SliderValue) this.config.create("Range Y", 5, 1, 10, 0).description("How big the affected radius should be in the Y dimension");
//    final BooleanValue destroy = (BooleanValue) this.config.create("Destroy particles", false).description("Makes particles appear when a block gets destroyed");
    final DoubleSetting rangeX = this.config.create(new DoubleSetting.Builder(5)
            .name("Range X")
            .description("How big of an area to fill in the X direction")
            .min(1)
            .max(10)
            .precision(0)
            .get());
    final DoubleSetting rangeY = this.config.create(new DoubleSetting.Builder(5)
            .name("Range Y")
            .description("How big of an area to fill in the Y direction")
            .min(1)
            .max(10)
            .precision(0)
            .get());
    final DoubleSetting rangeZ = this.config.create(new DoubleSetting.Builder(5)
            .name("Range Z")
            .description("How big of an area to fill in the Z direction")
            .min(1)
            .max(10)
            .precision(0)
            .get());
    final BooleanSetting destroy = this.config.create(new BooleanSetting.Builder(false)
            .name("Destroy particles")
            .description("makes the block breaking particles appear")
            .get());


    public ClickNuke() {
        super("ClickNuke", "Nukes whatever you click at, requires /fill permissions", ModuleType.WORLD);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (client.player == null) {
                return;
            }
            MouseEvent event1 = (MouseEvent) event;
            if (event1.getButton() == 0 && event1.getAction() == 1) {
                mousePressed();
            }
        });
    }

    void mousePressed() {
        if (client.currentScreen != null) {
            return;
        }
        HitResult hr = Objects.requireNonNull(client.player).raycast(200d, 0f, true);
        Vec3d pos1 = hr.getPos();
        BlockPos pos = new BlockPos(pos1);
        int startY = MathHelper.clamp(r(pos.getY() - rangeY.getValue()), Objects.requireNonNull(CoffeeClientMain.client.world).getBottomY(), CoffeeClientMain.client.world.getTopY());
        int endY = MathHelper.clamp(r(pos.getY() + rangeY.getValue()), CoffeeClientMain.client.world.getBottomY(), CoffeeClientMain.client.world.getTopY());
        String cmd = "/fill " + r(pos.getX() - rangeX.getValue()) + " " + startY + " " + r(pos.getZ() - rangeZ.getValue()) + " " + r(pos.getX() + rangeX.getValue()) + " " + endY + " " + r(pos.getZ() + rangeZ.getValue()) + " " + "minecraft:air" + (destroy.getValue() ? " destroy" : "");
        System.out.println(cmd);
        client.player.sendChatMessage(cmd);
    }

    int r(double v) {
        return (int) Math.round(v);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {

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

