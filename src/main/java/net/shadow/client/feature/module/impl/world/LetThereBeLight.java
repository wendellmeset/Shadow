/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.notifications.Notification;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.render.Renderer;

import java.awt.*;
import java.util.List;
import java.util.*;

public class LetThereBeLight extends Module {
    boolean noBlocksAck = false;

    public LetThereBeLight() {
        super("LetThereBeLight", "Places torches everywhere", ModuleType.WORLD);
    }

    @Override
    public void tick() {
        int torchSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack is = Objects.requireNonNull(ShadowMain.client.player).getInventory().getStack(i);
            if (is.getItem() == Items.TORCH || is.getItem() == Items.SOUL_TORCH) {
                torchSlot = i;
                break;
            }
        }
        if (torchSlot == -1) {
            if (!noBlocksAck) {
                Notification.create(6000, "AutoTorch", false, Notification.Type.WARNING, "Out of torches!");
                noBlocksAck = true;
            }
        } else {
            noBlocksAck = false;
        }
        Vec3d ppos = ShadowMain.client.player.getPos();
        Vec3d camPos = ShadowMain.client.player.getCameraPosVec(1);
        a:
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                List<Vec3d> blocksWithShit = new ArrayList<>();
                for (int y = Objects.requireNonNull(ShadowMain.client.world).getTopY(); y > ShadowMain.client.world.getBottomY(); y--) {
                    BlockPos bp = new BlockPos(ppos).add(x, y, z);
                    BlockState bs = ShadowMain.client.world.getBlockState(bp);
                    if (bs.getMaterial().isReplaceable() && Blocks.TORCH.getDefaultState().canPlaceAt(ShadowMain.client.world, bp)) {
                        blocksWithShit.add(Vec3d.of(bp));
                    }
                }
                Optional<Vec3d> real = blocksWithShit.stream().filter(vec3d -> vec3d.add(.5, .5, .5).distanceTo(camPos) <= 4).min(Comparator.comparingDouble(value -> value.distanceTo(camPos)));
                if (real.isEmpty()) {
                    continue; // nowhere to place
                }
                BlockHitResult bhr = new BlockHitResult(real.get(), Direction.DOWN, new BlockPos(real.get()), false);
                Objects.requireNonNull(ShadowMain.client.interactionManager).interactBlock(ShadowMain.client.player, ShadowMain.client.world, Hand.MAIN_HAND, bhr);
                break a;
            }
        }
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return noBlocksAck ? "No torches!" : null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        Vec3d ppos = Objects.requireNonNull(ShadowMain.client.player).getPos();
        Vec3d camPos = ShadowMain.client.player.getCameraPosVec(1);
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                List<Vec3d> blocksWithShit = new ArrayList<>();
                for (int y = Objects.requireNonNull(ShadowMain.client.world).getTopY(); y > ShadowMain.client.world.getBottomY(); y--) {
                    BlockPos bp = new BlockPos(ppos).add(x, y, z);
                    BlockState bs = ShadowMain.client.world.getBlockState(bp);
                    if (bs.getMaterial().isReplaceable() && Blocks.TORCH.getDefaultState().canPlaceAt(ShadowMain.client.world, bp)) {
                        blocksWithShit.add(Vec3d.of(bp));
                    }
                }
                Optional<Vec3d> real = blocksWithShit.stream().filter(vec3d -> vec3d.add(.5, .5, .5).distanceTo(camPos) <= 4).min(Comparator.comparingDouble(value -> value.distanceTo(camPos)));
                if (real.isEmpty()) {
                    continue; // nowhere to place
                }
                Vec3d pos = real.get();
                Renderer.R3D.renderShape(pos, Blocks.TORCH.getDefaultState().getOutlineShape(ShadowMain.client.world, new BlockPos(real.get()), ShapeContext.absent()), matrices, Color.WHITE);
                //                Renderer.R3D.renderOutline(real.get(),new Vec3d(1,1,1), Color.WHITE, matrices);
            }
        }
    }

    @Override
    public void onHudRender() {

    }
}
