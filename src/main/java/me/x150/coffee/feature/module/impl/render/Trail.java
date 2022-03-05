package me.x150.coffee.feature.module.impl.render;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import me.x150.coffee.helper.render.Renderer;
import me.x150.coffee.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Trail extends Module {
    List<Vec3d> positions = new ArrayList<>();

    public Trail() {
        super("Trail", "Leaves behind a short trail when you travel", ModuleType.RENDER);
    }

    @Override
    public void tick() {

    }

    @Override
    public void onFastTick() {
        positions.add(Utils.getInterpolatedEntityPosition(CoffeeClientMain.client.player));
        while (positions.size() > 1000) {
            positions.remove(0);
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
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {
        List<Vec3d> bk = new ArrayList<>(positions);
        float progressOffset = (System.currentTimeMillis() % 1000) / 1000f;
        for (int i = 0; i < bk.size(); i++) {
            float progress = i / (float) bk.size();
            progress += progressOffset;
            progress %= 1;
            Vec3d vec3d = bk.get(i);
            if (vec3d == null) {
                continue;
            }
            Vec3d before;
            if (i == 0) {
                before = vec3d;
            } else {
                before = bk.get(i - 1);
            }
            if (before == null) {
                before = vec3d;
            }
            Renderer.R3D.renderLine(before, vec3d, Color.getHSBColor(progress, 0.6f, 1f), matrices);
        }

    }

    @Override
    public void onHudRender() {

    }
}
