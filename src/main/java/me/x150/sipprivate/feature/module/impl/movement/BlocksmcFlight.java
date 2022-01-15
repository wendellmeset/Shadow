package me.x150.sipprivate.feature.module.impl.movement;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BlocksmcFlight extends Module {
    int jumpTimeout = 0;
    double yStart = 0;
    public BlocksmcFlight() {
        super("BlocksMCFlight", "Bypasses the blocksmc anticheat and flies", ModuleType.MOVEMENT);
    }
    @Override
    public void tick() {
        jumpTimeout--;
        if (jumpTimeout < 0) jumpTimeout = 0;
        if (CoffeeClientMain.client.player.getPos().y < yStart && jumpTimeout == 0) {
            CoffeeClientMain.client.player.jump();
            jumpTimeout = 5;
        }
    }

    @Override
    public void enable() {
        yStart = CoffeeClientMain.client.player.getPos().y;
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
        Vec3d ppos = Utils.getInterpolatedEntityPosition(CoffeeClientMain.client.player);
        Vec3d renderPos = new Vec3d(ppos.x,yStart,ppos.z);
        Renderer.R3D.renderOutline(renderPos.subtract(1,0,1),new Vec3d(2,0,2), Color.RED,matrices);
    }

    @Override
    public void onHudRender() {

    }
}
