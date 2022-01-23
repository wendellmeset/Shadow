package me.x150.sipprivate.feature.module.impl.world;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.notifications.Notification;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AutoTnt extends Module {
    boolean missingTntAck = false;

    public AutoTnt() {
        super("AutoTNT", "Automatically places tnt in a grid", ModuleType.WORLD);
    }

    @Override
    public void tick() {
        int tntSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack is = Objects.requireNonNull(CoffeeClientMain.client.player).getInventory().getStack(i);
            if (is.getItem() == Items.TNT) {
                tntSlot = i;
                break;
            }
        }
        if (tntSlot == -1) {
            if (!missingTntAck) {
                Notification.create(6000, "AutoTNT", false, Notification.Type.WARNING, "Ran out of tnt! Get more in your hotbar");
            }
            missingTntAck = true;
            return;
        } else {
            missingTntAck = false;
        }

        Vec3d ppos = CoffeeClientMain.client.player.getPos();
        for (double x = -10; x < 11; x++) {
            for (double z = -10; z < 11; z++) {
                List<Map.Entry<BlockPos, Double>> airs = new ArrayList<>();

                for (int y = Objects.requireNonNull(CoffeeClientMain.client.world).getTopY(); y > CoffeeClientMain.client.world.getBottomY(); y--) {
                    Vec3d currentOffset = new Vec3d(x, y, z);
                    BlockPos bp = new BlockPos(new Vec3d(ppos.x + currentOffset.x, y, ppos.z + currentOffset.z));
                    BlockState bs = CoffeeClientMain.client.world.getBlockState(bp);
                    double dist = Vec3d.of(bp).distanceTo(ppos);
                    if (bs.getMaterial().isReplaceable()) {
                        airs.add(new AbstractMap.SimpleEntry<>(bp, dist));
                    }
                }
                airs = airs.stream().filter(blockPosDoubleEntry -> CoffeeClientMain.client.world.getBlockState(blockPosDoubleEntry.getKey().down()).getMaterial().blocksMovement())
                        .collect(Collectors.toList());
                Map.Entry<BlockPos, Double> best1 = airs.stream().min(Comparator.comparingDouble(Map.Entry::getValue)).orElse(null);
                if (best1 == null) {
                    continue; // just void here, cancel
                }
                BlockPos best = best1.getKey();
                if (CoffeeClientMain.client.world.getBlockState(best.down()).getBlock() == Blocks.TNT) {
                    continue; // already placed tnt, cancel
                }
                Vec3d lmao = Vec3d.of(best);
                if (lmao.add(.5, .5, .5).distanceTo(CoffeeClientMain.client.player.getCameraPosVec(1)) >= 5) {
                    continue;
                }
                if (shouldPlace(best)) {
                    int finalTntSlot = tntSlot;
                    CoffeeClientMain.client.execute(() -> {
                        int sel = CoffeeClientMain.client.player.getInventory().selectedSlot;
                        CoffeeClientMain.client.player.getInventory().selectedSlot = finalTntSlot;
                        BlockHitResult bhr = new BlockHitResult(lmao, Direction.DOWN, best, false);
                        Objects.requireNonNull(CoffeeClientMain.client.interactionManager).interactBlock(CoffeeClientMain.client.player, CoffeeClientMain.client.world, Hand.MAIN_HAND, bhr);
                        CoffeeClientMain.client.player.getInventory().selectedSlot = sel;
                    });
                }
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
        return missingTntAck ? "Missing tnt!" : null;
    }

    boolean shouldPlace(BlockPos b) {
        return b.getX() % 4 == 0 && b.getZ() % 4 == 0;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        Vec3d ppos = Objects.requireNonNull(CoffeeClientMain.client.player).getPos();

        for (double x = -10; x < 11; x++) {
            for (double z = -10; z < 11; z++) {
                List<Map.Entry<BlockPos, Double>> airs = new ArrayList<>();

                for (int y = Objects.requireNonNull(CoffeeClientMain.client.world).getTopY(); y > CoffeeClientMain.client.world.getBottomY(); y--) {
                    Vec3d currentOffset = new Vec3d(x, y, z);
                    BlockPos bp = new BlockPos(new Vec3d(ppos.x + currentOffset.x, y, ppos.z + currentOffset.z));
                    BlockState bs = CoffeeClientMain.client.world.getBlockState(bp);
                    double dist = Vec3d.of(bp).distanceTo(ppos);
                    if (bs.getMaterial().isReplaceable()) {
                        airs.add(new AbstractMap.SimpleEntry<>(bp, dist));
                    }
                }
                airs = airs.stream().filter(blockPosDoubleEntry -> CoffeeClientMain.client.world.getBlockState(blockPosDoubleEntry.getKey().down()).getMaterial().blocksMovement())
                        .collect(Collectors.toList());
                Map.Entry<BlockPos, Double> best1 = airs.stream().min(Comparator.comparingDouble(Map.Entry::getValue)).orElse(null);
                if (best1 == null) {
                    continue; // just void here, cancel
                }
                BlockPos best = best1.getKey();
                if (CoffeeClientMain.client.world.getBlockState(best.down()).getBlock() == Blocks.TNT) {
                    continue; // already placed tnt, cancel
                }
                Vec3d lmao = Vec3d.of(best);
                if (shouldPlace(best)) {
                    Renderer.R3D.renderOutline(lmao, new Vec3d(1, 1, 1), Color.RED, matrices);
                }
            }
        }
    }

    @Override
    public void onHudRender() {

    }
}
