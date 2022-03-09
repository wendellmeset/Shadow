package net.shadow.client.feature.module.impl.fun;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.ColorSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

import java.awt.*;

public class Test extends Module {
    ColorSetting cs = this.config.create(new ColorSetting.Builder(Color.WHITE)
            .name("REAL")
            .description("REAL")
            .get());

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.FUN);
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
    public void onWorldRender(MatrixStack s) {
        Camera camera = ShadowMain.client.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        MatrixStack stack = new MatrixStack();
        stack.push();
        //        stack.translate(start.x, start.y, start.z);
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
        stack.translate(-camPos.x, -camPos.y, -camPos.z);

        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        int color = cs.getValue().getRGB();


        Matrix4f matrix = stack.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        double segments = 20;
        double rad = 1;
        for (double r = 0; r <= 360; r += (360 / segments)) {
            double rad1 = Math.toRadians(r);
            double sin = Math.sin(rad1);
            double cos = Math.cos(rad1);
            double offX = sin * rad;
            double offY = cos * rad;
            bufferBuilder.vertex(matrix, 0, 0.3f, 0).color(1f, 1f, 1f, 1f).next();
            bufferBuilder.vertex(matrix, (float) offX, 0, (float) offY).color(g, h, k, f).next();
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        stack.pop();

    }

    @Override
    public void onHudRender() {

    }
}
