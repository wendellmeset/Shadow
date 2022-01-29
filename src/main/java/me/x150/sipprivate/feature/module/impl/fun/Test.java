package me.x150.sipprivate.feature.module.impl.fun;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Test extends Module {
    Vec3d epos;
    List<AnimatedCircle> ac = new ArrayList<>();

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.FUN);
    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {
        epos = CoffeeClientMain.client.player.getPos();
        ac.clear();
        for (int i = 0; i < 3; i++) {
            double initialProg = i / 3d;
            AnimatedCircle a = new AnimatedCircle();
            a.spawnPos = epos;
            a.animProg = initialProg;
            ac.add(a);
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
    public void onFastTick() {
        for (AnimatedCircle animatedCircle : ac) {
            animatedCircle.tick();
        }
        super.onFastTick();
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        for (AnimatedCircle animatedCircle : ac) {
            animatedCircle.render(matrices);
        }
    }

    @Override
    public void onHudRender() {

    }

    static class AnimatedCircle {
        double animProg = 0;
        Vec3d spawnPos;
        Color a = new Color(197, 37, 37);
        Color b = new Color(200, 200, 200, 0);

        public void render(MatrixStack stack) {
            double progI = animProg * 2;
            double expandProg = progI / 2d; // 0-2 of 0-2 as 0-1
            double colorProg = MathHelper.clamp(progI - 1, 0, 1); // 1-2 of 0-2 as 0-1
            Color color = Renderer.Util.lerp(b, a, colorProg);
            double width = expandProg * 5;
            Renderer.R3D.renderCircleOutline(stack, color, spawnPos, width, 0.03, 50);
        }

        void tick() {
            animProg += 0.005;
            animProg %= 1;
        }
    }
}
