/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.feature.config.BooleanSetting;
import me.x150.sipprivate.feature.config.DoubleSetting;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.Rotations;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

public class Nuker extends Module {

    final List<BlockPos> renders = new ArrayList<>();
    //    final SliderValue    range             = (SliderValue) this.config.create("Range", 3, 0, 4, 1).description("The range to nuke by");
//    final SliderValue    blocksPerTick     = (SliderValue) this.config.create("Blocks per tick", 1, 1, 20, 0).description("The amount of blocks to destroy per tick");
//    final SliderValue    delay             = (SliderValue) this.config.create("Delay", 5, 0, 20, 0).description("The delay before breaking blocks");
//    final BooleanValue ignoreXray        = (BooleanValue) this.config.create("Ignore xray", true).description("Whether or not to ignore xray blocks");
//    final MultiValue     mode              = (MultiValue) this.config.create("Mode", "Everything", "Everything", "Torches", "Fire", "Wood", "Grass").description("What to nuke");
//    final BooleanValue   autoTool          = (BooleanValue) this.config.create("Auto tool", true).description("Automatically picks the best tool from your inventory, for the block being broken");
    final Block[] WOOD = new Block[]{Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG};
//    final MultiValue     mv                = (MultiValue) this.config.create("Sort", "Out -> In", "Out -> In", "In -> Out", "Strength", "Random").description("How to sort");
//    final BooleanValue   ignoreUnbreakable = (BooleanValue) this.config.create("Ignore unbreakable", true).description("Ignore survival unbreakable blocks");

    final DoubleSetting range = this.config.create(new DoubleSetting.Builder(4)
            .name("Range")
            .description("How far to break blocks")
            .min(0)
            .max(4)
            .precision(1)
            .get());
    final DoubleSetting blocksPerTick = this.config.create(new DoubleSetting.Builder(1)
            .name("Blocks per tick")
            .description("How many blocks to break per tick")
            .min(1)
            .max(20)
            .precision(0)
            .get());
    final DoubleSetting delay = this.config.create(new DoubleSetting.Builder(0)
            .name("Delay")
            .description("How much to wait between ticks")
            .min(0)
            .max(20)
            .precision(0)
            .get());
    final BooleanSetting ignoreXray = this.config.create(new BooleanSetting.Builder(true)
            .name("Ignore XRAY")
            .description("Ignores XRAY blocks")
            .get());
    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Everything)
            .name("Mode")
            .description("What to break")
            .get());
    final BooleanSetting autoTool = this.config.create(new BooleanSetting.Builder(true)
            .name("Auto tool")
            .description("Automatically picks the best tool for the block")
            .get());
    final EnumSetting<SortMode> mv = this.config.create(new EnumSetting.Builder<>(SortMode.OutIn)
            .name("Sorting")
            .description("In which order to break the blocks")
            .get());
    final BooleanSetting ignoreUnbreakable = this.config.create(new BooleanSetting.Builder(true)
            .name("Ignore unbreakable")
            .description("Ignores unbreakable blocks")
            .get());
    int delayPassed = 0;

    public Nuker() {
        super("Nuker", "Breaks a lot of blocks around you fast", ModuleType.WORLD);
    }

    boolean isBlockApplicable(Block b) {
        if (mode.getValue() == Mode.Everything) {
            return true;
        } else if (mode.getValue() == Mode.Torches) {
            return b == Blocks.TORCH || b == Blocks.WALL_TORCH || b == Blocks.SOUL_TORCH || b == Blocks.SOUL_WALL_TORCH;
        } else if (mode.getValue() == Mode.Fire) {
            return b == Blocks.FIRE || b == Blocks.SOUL_FIRE;
        } else if (mode.getValue() == Mode.Wood) {
            return Arrays.stream(WOOD).anyMatch(block -> block == b);
        } else if (mode.getValue() == Mode.Grass) {
            return b == Blocks.GRASS || b == Blocks.TALL_GRASS;
        }
        return false;
    }

    @Override
    public void tick() {
        if (client.player == null || client.world == null || client.interactionManager == null || client.getNetworkHandler() == null) {
            return;
        }
        if (delayPassed < delay.getValue()) {
            delayPassed++;
            return;
        }
        delayPassed = 0;
        BlockPos ppos1 = client.player.getBlockPos();
        int blocksBroken = 0;
        renders.clear();
        List<BlockPos> toHit = new ArrayList<>();
        for (double y = range.getValue(); y > -range.getValue() - 1; y--) {
            for (double x = -range.getValue(); x < range.getValue() + 1; x++) {
                for (double z = -range.getValue(); z < range.getValue() + 1; z++) {
                    BlockPos vp = new BlockPos(x, y, z);
                    BlockPos np = ppos1.add(vp);
                    Vec3d vp1 = Vec3d.of(np).add(.5, .5, .5);
                    if (vp1.distanceTo(client.player.getEyePos()) >= client.interactionManager.getReachDistance()) {
                        continue;
                    }
                    toHit.add(np);
                }
            }
        }
        toHit = toHit.stream().sorted(Comparator.comparingDouble(value1 -> {
            Vec3d value = Vec3d.of(value1).add(new Vec3d(.5, .5, .5));
            return switch (mv.getValue()) {
                case OutIn -> value.distanceTo(client.player.getPos()) * -1;
                case InOut -> value.distanceTo(client.player.getPos());
                case Strength -> client.world.getBlockState(value1).getBlock().getHardness();
                default -> 1;
            };
        })).collect(Collectors.toList());
        if (mv.getValue() == SortMode.Random) {
            Collections.shuffle(toHit);
        }
        for (BlockPos np : toHit) {
            if (blocksBroken >= blocksPerTick.getValue()) {
                break;
            }
            BlockState bs = client.world.getBlockState(np);
            boolean b = !ignoreXray.getValue() || !XRAY.blocks.contains(bs.getBlock());
            if (!bs.isAir() && bs.getBlock() != Blocks.WATER && bs.getBlock() != Blocks.LAVA && !isUnbreakable(bs.getBlock()) && b && client.world.getWorldBorder()
                    .contains(np) && isBlockApplicable(bs.getBlock())) {
                renders.add(np);
                if (autoTool.getValue()) {
                    AutoTool.pick(bs);
                }
                client.player.swingHand(Hand.MAIN_HAND);
                if (!client.player.getAbilities().creativeMode) {
                    client.interactionManager.updateBlockBreakingProgress(np, Direction.DOWN);
                } else {
                    client.interactionManager.attackBlock(np, Direction.DOWN);
                }
                Rotations.lookAtV3(new Vec3d(np.getX() + .5, np.getY() + .5, np.getZ() + .5));
                blocksBroken++;
            }
        }
    }

    boolean isUnbreakable(Block b) {
        if (!ignoreUnbreakable.getValue()) {
            return false;
        }
        return b.getHardness() == -1;
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
        Everything, Torches, Fire, Wood, Grass
    }

    public enum SortMode {
        OutIn, InOut, Strength, Random
    }
}

