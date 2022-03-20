/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.BooleanSetting;
import net.shadow.client.feature.config.DoubleSetting;
import net.shadow.client.feature.config.EnumSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.Rotations;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Nuker extends Module {

    final List<BlockPos> renders = new ArrayList<>();
    final Block[] WOOD = new Block[]{Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG};

    final DoubleSetting range = this.config.create(new DoubleSetting.Builder(4).name("Range").description("How far to break blocks").min(0).max(8).precision(1).get());
    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Packet).name("Mode").description("What to break").get());
    int delayPassed = 0;

    public Nuker() {
        super("Nuker", "Breaks a lot of blocks around you fast", ModuleType.GRIEF);
    }

    @Override
    public void tick() {
        renders.clear();
        if (client.player == null || client.world == null || client.interactionManager == null || client.getNetworkHandler() == null) {
            return;
        }
        if (mode.getValue().equals(Mode.Interact)) {
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = ShadowMain.client.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(ShadowMain.client.player.getPos()) > range.getValue() || ShadowMain.client.world.getBlockState(pos).isAir() || ShadowMain.client.world.getBlockState(pos).getBlock() == Blocks.WATER || ShadowMain.client.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                            continue;
                        renders.add(pos);
                        ShadowMain.client.interactionManager.attackBlock(pos, Direction.DOWN);
                    }
        } else if (mode.getValue().equals(Mode.Packet)){
            for (int x = -7; x < 8; x++)
                for (int y = -7; y < 8; y++)
                    for (int z = -7; z < 8; z++) {
                        BlockPos pos = ShadowMain.client.player.getBlockPos().add(new BlockPos(x, y, z));
                        if (new Vec3d(pos.getX(), pos.getY(), pos.getZ()).distanceTo(ShadowMain.client.player.getPos()) > range.getValue() || ShadowMain.client.world.getBlockState(pos).isAir() || ShadowMain.client.world.getBlockState(pos).getBlock() == Blocks.WATER || ShadowMain.client.world.getBlockState(pos).getBlock() == Blocks.LAVA)
                            continue;
                        renders.add(pos);
                        ShadowMain.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.DOWN));
                        ShadowMain.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.DOWN));
            }
        }
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        for (BlockPos render : renders) {
            Vec3d vp = new Vec3d(render.getX(), render.getY(), render.getZ());
            Renderer.R3D.renderFilled(vp, new Vec3d(1, 1, 1), Renderer.Util.modify(Utils.getCurrentRGB(), -1, -1, -1, 50), matrices);
        }
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
    }

    @Override
    public void onHudRender() {

    }

    public enum Mode {
        Packet, Interact
    }
}

