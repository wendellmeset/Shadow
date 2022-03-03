package me.x150.coffee.feature.module.impl.world;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.config.EnumSetting;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import me.x150.coffee.helper.Rotations;
import me.x150.coffee.helper.Timer;
import me.x150.coffee.helper.render.Renderer;
import net.minecraft.block.Block;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.Color;

public class AutoLavacast extends Module {

    static boolean moveForwards = false;
    EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Bypass).name("Mode").description("How to place and move. Bypass is slow but looks legit, fast is VERY speedy").get());

    Input    original;
    Timer    timer = new Timer();
    Vec3i    incr;
    BlockPos start;

    public AutoLavacast() {
        super("AutoLavacast", "Makes a lavacast", ModuleType.WORLD);
        mode.showIf(() -> !this.isEnabled()); // only show when disabled to prevent changes mid action
    }

    BlockPos getNextPosition() {
        int y = 0;
        while ((y + start.getY()) < CoffeeClientMain.client.world.getTopY()) {
            Vec3i ie = incr.multiply(y + 1);
            BlockPos next = start.add(ie).add(0, y, 0);
            if (CoffeeClientMain.client.world.getBlockState(next).getMaterial().isReplaceable()) {
                return next;
            }
            y++;
        }
        return null;
    }

    @Override public void onFastTick() {
        if (mode.getValue() == Mode.Fast && !timer.hasExpired(100)) {
            return;
        }
        timer.reset();
        BlockPos next = getNextPosition();
        if (next == null) {
            setEnabled(false);
            return;
        }
        Vec3d placeCenter = Vec3d.of(next).add(.5, .5, .5);
        if (mode.getValue() == Mode.Bypass) {
            Rotations.lookAtPositionSmooth(placeCenter, 6);
            if (((CoffeeClientMain.client.player.horizontalCollision && moveForwards) || CoffeeClientMain.client.player.getBoundingBox()
                    .intersects(Vec3d.of(next), Vec3d.of(next).add(1, 1, 1))) && CoffeeClientMain.client.player.isOnGround()) {
                CoffeeClientMain.client.player.jump();
                CoffeeClientMain.client.player.setOnGround(false);
            }
        }

        if (placeCenter.distanceTo(CoffeeClientMain.client.player.getCameraPosVec(1)) < CoffeeClientMain.client.interactionManager.getReachDistance()) {
            moveForwards = false;

            ItemStack is = CoffeeClientMain.client.player.getInventory().getMainHandStack();
            if (is.isEmpty()) {
                return;
            }
            if (is.getItem() instanceof BlockItem bi) {
                Block p = bi.getBlock();
                if (p.getDefaultState().canPlaceAt(CoffeeClientMain.client.world, next)) {
                    CoffeeClientMain.client.execute(() -> {
                        BlockHitResult bhr = new BlockHitResult(placeCenter, Direction.DOWN, next, false);
                        CoffeeClientMain.client.interactionManager.interactBlock(CoffeeClientMain.client.player, CoffeeClientMain.client.world, Hand.MAIN_HAND, bhr);
                        if (mode.getValue() == Mode.Fast) {
                            Vec3d goP = Vec3d.of(next).add(0.5, 1.05, 0.5);
                            CoffeeClientMain.client.player.updatePosition(goP.x, goP.y, goP.z);
                        }
                    });
                }
            }

        } else {
            moveForwards = true;
        }
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        if (original == null) {
            original = CoffeeClientMain.client.player.input;
        }
        if (mode.getValue() == Mode.Bypass) {
            CoffeeClientMain.client.player.input = new ListenInput();
        }
        incr = CoffeeClientMain.client.player.getMovementDirection().getVector();
        start = CoffeeClientMain.client.player.getBlockPos();
    }

    @Override public void disable() {
        CoffeeClientMain.client.player.input = original;
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        BlockPos next = getNextPosition();
        Renderer.R3D.renderOutline(Vec3d.of(start), new Vec3d(1, 0.01, 1), Color.RED, matrices);
        if (next != null) {
            Renderer.R3D.renderOutline(Vec3d.of(next), new Vec3d(1, 1, 1), Color.BLUE, matrices);
        }
    }

    @Override public void onHudRender() {

    }

    public enum Mode {
        Bypass, Fast
    }

    static class ListenInput extends Input {
        @Override public void tick(boolean slowDown) {
            this.movementForward = moveForwards ? 1 : 0;
        }
    }
}
