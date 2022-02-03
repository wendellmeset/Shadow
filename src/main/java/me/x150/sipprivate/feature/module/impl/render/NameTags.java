package me.x150.sipprivate.feature.module.impl.render;

import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static me.x150.sipprivate.feature.module.impl.render.TargetHud.GREEN;
import static me.x150.sipprivate.feature.module.impl.render.TargetHud.RED;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags", "Shows information about players above them", ModuleType.RENDER);
    }


    @Override
    public void tick() {

    }

    @Override
    public void enable() {

    }

    public void render(MatrixStack stack, AbstractClientPlayerEntity entity, Text text) {
        String t = text.getString();
        double healthHeight = 2;
        double labelHeight = 2 + FontRenderers.getNormal().getFontHeight() + 2 + healthHeight + 2;
        Vec3d headPos = Utils.getInterpolatedEntityPosition(entity).add(0, entity.getHeight() * 2 + 0.3, 0);
        Vec3d a = Renderer.R2D.getScreenSpaceCoordinate(headPos, stack);
        if (Renderer.R2D.isOnScreen(a)) {
            Utils.TickManager.runOnNextRender(() -> {
                MatrixStack stack1 = Renderer.R3D.getEmptyMatrixStack();
                Vec3d actual = new Vec3d(a.x, a.y - labelHeight, a.z);
                float fontWidth = FontRenderers.getNormal().getStringWidth(t) + 1 + 4;
                float width = fontWidth;
                width = Math.max(width, 60);
                Renderer.R2D.renderRoundedQuad(stack1, new Color(0, 0, 0, 200), actual.x - width / 2d, actual.y, actual.x + width / 2d, actual.y + labelHeight, 3, 20);
//                FontRenderers.getNormal().drawCenteredString(stack,t,actual.x,actual.y+2,0xFFFFFF);
                FontRenderers.getNormal().drawString(stack1, t, actual.x - fontWidth / 2d + 2, actual.y + 2, 0xFFFFFF);
                Renderer.R2D.renderRoundedQuad(stack1, new Color(60, 60, 60, 255), actual.x - width / 2d + 2, actual.y + labelHeight - 2 - healthHeight, actual.x + width / 2d - 2, actual.y + labelHeight - 2, healthHeight / 2d, 10);
                float health = entity.getHealth();
                float maxHealth = entity.getMaxHealth();
                float healthPer = health / maxHealth;
                healthPer = MathHelper.clamp(healthPer, 0, 1);
                double drawTo = MathHelper.lerp(healthPer, actual.x - width / 2d + 2 + healthHeight, actual.x + width / 2d - 2);
                Color MID_END = Renderer.Util.lerp(GREEN, RED, healthPer);
                Renderer.R2D.renderRoundedQuad(stack1, MID_END, actual.x - width / 2d + 2, actual.y + labelHeight - 2 - healthHeight, drawTo, actual.y + labelHeight - 2, healthHeight / 2d, 10);
            });
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
}
