/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.fun;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.ColorSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.render.Renderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Test extends Module {
    ColorSetting color = this.config.create(new ColorSetting.Builder(Color.WHITE)
            .name("REAL")
            .description("REAL")
            .get());

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.MISC);
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
    public void onWorldRender(MatrixStack matrices) {

        Color color = this.color.getValue();
        Vec3d start = new Vec3d(0, 100, 0);

        Camera c = ShadowMain.client.gameRenderer.getCamera();
        Vec3d camPos = c.getPos();
        start = start.subtract(camPos);
        float x1 = (float) start.x;
        float y1 = (float) start.y;
        float z1 = (float) start.z;
        matrices.push();
        matrices.translate(x1, y1, z1);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        //            RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
//        RenderSystem.disableCull();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        Icosahedron.drawIcosahedron(matrix, buffer, 1f, 1f, 1f, 1f, 1, 2);

        buffer.end();

        BufferRenderer.draw(buffer);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        RenderSystem.disableBlend();
        matrices.pop();

    }

    @Override
    public void onHudRender() {

    }

    record Vertex(double x, double y, double z) {
    }

    class Icosahedron {

        public static double X = 0.525731112119133606f;
        public static double Z = 0.850650808352039932f;

        public static double[][] vdata = {{-X, +0, +Z}, {+X, +0, +Z}, {-X, +0, -Z}, {+X, +0, -Z}, {+0, +Z, +X}, {+0, +Z, -X},
                {+0, -Z, +X}, {+0, -Z, -X}, {+Z, +X, +0}, {-Z, +X, +0}, {+Z, -X, +0}, {-Z, -X, +0}};

        public static int[][] tindx = {{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1}, {8, 10, 1}, {8, 3, 10},
                {5, 3, 8}, {5, 2, 3}, {2, 7, 3}, {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6}, {6, 1, 10},
                {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}};


        public static void drawIcosahedron(Matrix4f matrix, BufferBuilder bb, float r, float g, float b, float a, int depth, float radius) {
            for (int i = 0; i < tindx.length; i++) {
                int[] ints = tindx[i];
                double index = (double) i / tindx.length;
                Color c = Renderer.Util.lerp(Color.RED, Color.GREEN, index);
                subdivide(matrix, bb, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, a,
                        vdata[ints[0]], //
                        vdata[ints[1]], //
                        vdata[ints[2]], depth, radius);
            }
        }

        private static void subdivide(Matrix4f matrix, BufferBuilder bb, float r, float g, float b, float a, double[] vA0, double[] vB1, double[] vC2, int depth, float radius) {

            double[] vAB = new double[3];
            double[] vBC = new double[3];
            double[] vCA = new double[3];

            int i;

            if (depth == 0) {
                drawTriangle(matrix, bb, r, g, b, a, vA0, vB1, vC2, radius);

                return;
            }

            for (i = 0; i < 3; i++) {
                vAB[i] = (vA0[i] + vB1[i]) / 2;
                vBC[i] = (vB1[i] + vC2[i]) / 2;
                vCA[i] = (vC2[i] + vA0[i]) / 2;
            }

            double modAB = mod(vAB);
            double modBC = mod(vBC);
            double modCA = mod(vCA);

            for (i = 0; i < 3; i++) {
                vAB[i] /= modAB;
                vBC[i] /= modBC;
                vCA[i] /= modCA;
            }

            subdivide(matrix, bb, r, g, b, a, vA0, vAB, vCA, depth - 1, radius);
            subdivide(matrix, bb, r, g, b, a, vB1, vBC, vAB, depth - 1, radius);
            subdivide(matrix, bb, r, g, b, a, vC2, vCA, vBC, depth - 1, radius);
            subdivide(matrix, bb, r, g, b, a, vAB, vBC, vCA, depth - 1, radius);
        }


        public static double mod(double[] v) {
            return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        }

        // --------------------------------------------------------------------------------

        private static double[] calcTextureMap(double[] vtx) {
            double[] ret = new double[3];

            ret[0] = Math.sqrt(vtx[0] * vtx[0] + vtx[1] * vtx[1] + vtx[2] * vtx[2]);
            ret[1] = Math.acos(vtx[2] / ret[0]);
            ret[2] = Math.atan2(vtx[1], vtx[0]);

            ret[1] += Math.PI;
            ret[1] /= (2 * Math.PI);
            ret[2] += Math.PI;
            ret[2] /= (2 * Math.PI);

            return ret;
        }

        private static void drawTriangle(Matrix4f matrix, BufferBuilder bb, float r, float g, float b, float a, double[] v1, double[] v2, double[] v3, float radius) {
            double[] spherical;

//            Tessellator tessellator = Tessellator.getInstance();
//            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
//            worldrenderer.startDrawing(GL11.GL_TRIANGLES);

//            GL11.glEnable(GL11.GL_TEXTURE_2D);

            spherical = calcTextureMap(v1);
            bb.vertex(matrix, radius * (float) v1[0], radius * (float) v1[1], radius * (float) v1[2]).normal(1, 1, 1).color(r, g, b, a).next();
//            worldrenderer.addVertexWithUV(radius * v1[0], radius * v1[1], radius * v1[2], spherical[1], spherical[2]);

            spherical = calcTextureMap(v2);
            bb.vertex(matrix, radius * (float) v2[0], radius * (float) v2[1], radius * (float) v2[2]).normal(1, 1, 1).color(r, g, b, a).next();

            spherical = calcTextureMap(v3);
            bb.vertex(matrix, radius * (float) v3[0], radius * (float) v3[1], radius * (float) v3[2]).normal(1, 1, 1).color(r, g, b, a).next();

//            tessellator.draw();
        }
    }
}
