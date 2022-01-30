package me.x150.sipprivate.helper.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.math.Matrix4x4;
import me.x150.sipprivate.helper.math.Vector3D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Renderer {

    public static class R3D {

        static final MatrixStack empty = new MatrixStack();

        public static void renderCircleOutline(MatrixStack stack, Color c, Vec3d start, double rad, double width, double segments) {
            RenderSystem.disableCull();
            Camera camera = CoffeeClientMain.client.gameRenderer.getCamera();
            Vec3d camPos = camera.getPos();
            start = start.subtract(camPos);
            stack.push();
            stack.translate(start.x, start.y, start.z);
//            stack.multiply(new Quaternion(0, (System.currentTimeMillis() % 2000) / 2000f * 360f, 0, true));
            segments = MathHelper.clamp(segments, 2, 90);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int color = c.getRGB();

            Matrix4f matrix = stack.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (double r = 0; r < 360; r += (360 / segments)) {
                double rad1 = Math.toRadians(r);
                double sin = Math.sin(rad1);
                double cos = Math.cos(rad1);
                double offX = sin * rad;
                double offY = cos * rad;
                bufferBuilder.vertex(matrix, (float) offX, 0, (float) offY).color(g, h, k, f).next();
                bufferBuilder.vertex(matrix, (float) (offX + sin * width), 0, (float) (offY + cos * width)).color(g, h, k, f).next();

            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            stack.pop();
        }

        public static void renderOutlineIntern(Vec3d start, Vec3d dimensions, MatrixStack stack, BufferBuilder buffer) {
            Camera c = CoffeeClientMain.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            Vec3d end = start.add(dimensions);
            Matrix4f matrix = stack.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;

            buffer.vertex(matrix, x1, y1, z1).next();
            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x1, y1, z1).next();

            buffer.vertex(matrix, x1, y2, z1).next();
            buffer.vertex(matrix, x1, y2, z2).next();
            buffer.vertex(matrix, x1, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();
            buffer.vertex(matrix, x2, y2, z1).next();
            buffer.vertex(matrix, x2, y2, z1).next();
            buffer.vertex(matrix, x1, y2, z1).next();

            buffer.vertex(matrix, x1, y1, z1).next();
            buffer.vertex(matrix, x1, y2, z1).next();

            buffer.vertex(matrix, x2, y1, z1).next();
            buffer.vertex(matrix, x2, y2, z1).next();

            buffer.vertex(matrix, x2, y1, z2).next();
            buffer.vertex(matrix, x2, y2, z2).next();

            buffer.vertex(matrix, x1, y1, z2).next();
            buffer.vertex(matrix, x1, y2, z2).next();
        }

        //you can call renderOutlineIntern multiple times to save performance
        public static void renderOutline(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            BufferBuilder buffer = renderPrepare(color);

            renderOutlineIntern(start, dimensions, stack, buffer);

            buffer.end();
            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        public static BufferBuilder renderPrepare(Color color) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            RenderSystem.setShader(GameRenderer::getPositionShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(red, green, blue, alpha);
            //RenderSystem.lineWidth(2f);
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
            return buffer;
        }

        public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
            renderFilled(start, dimensions, color, stack, GL11.GL_ALWAYS);
        }

        public static MatrixStack getEmptyMatrixStack() {
            empty.loadIdentity(); // essentially clear the stack
            return empty;
        }

        public static void renderFilled(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack, int GLMODE) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            Camera c = CoffeeClientMain.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            Vec3d end = start.add(dimensions);
            Matrix4f matrix = stack.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GL11.glDepthFunc(GLMODE);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableCull();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();

            buffer.end();

            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
        }

        public static void renderShape(Vec3d start, VoxelShape shape, MatrixStack matrices, Color color) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            Camera c = CoffeeClientMain.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer.vertex(matrix, (float) (x1 + minX), (float) (y1 + minY), (float) (z1 + minZ)).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, (float) (x1 + maxX), (float) (y1 + maxY), (float) (z1 + maxZ)).color(red, green, blue, alpha).next();
            });

            buffer.end();

            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        public static void renderLine(Vec3d start, Vec3d end, Color color, MatrixStack matrices) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            Camera c = CoffeeClientMain.client.gameRenderer.getCamera();
            Vec3d camPos = c.getPos();
            start = start.subtract(camPos);
            end = end.subtract(camPos);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float x1 = (float) start.x;
            float y1 = (float) start.y;
            float z1 = (float) start.z;
            float x2 = (float) end.x;
            float y2 = (float) end.y;
            float z2 = (float) end.z;
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();

            buffer.end();

            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        public static Vec3d getCrosshairVector() {

            Camera camera = CoffeeClientMain.client.gameRenderer.getCamera();

            float vec = 0.017453292F;
            float pi = (float) Math.PI;

            float f1 = MathHelper.cos(-camera.getYaw() * vec - pi);
            float f2 = MathHelper.sin(-camera.getYaw() * vec - pi);
            float f3 = -MathHelper.cos(-camera.getPitch() * vec);
            float f4 = MathHelper.sin(-camera.getPitch() * vec);

            return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
        }

    }

    public static class R2D {

        public static Vec2f renderTooltip(MatrixStack stack, double arrowX, double arrowY, double width, double height, Color color) {
            return renderTooltip(stack, arrowX, arrowY, width, height, color, false);
        }

        /**
         * Renders an arrow tooltip
         *
         * @param stack  The transformation stack
         * @param arrowX the x position of the arrow
         * @param arrowY the y position of the arrow
         * @param width  the width of the tooltip
         * @param height the height of the tooltip
         * @param color  the color of the tooltip
         * @return the start position (0,0) of the tooltip content, after considering where to place it
         */
        public static Vec2f renderTooltip(MatrixStack stack, double arrowX, double arrowY, double width, double height, Color color, boolean renderUpsideDown) {
            double centerX = CoffeeClientMain.client.getWindow().getScaledWidth() / 2d;
            double centerY = CoffeeClientMain.client.getWindow().getScaledHeight() / 2d;
            /*
            left:
            *           /\
            * --------------
            * |            |
            * |            |
            * --------------
            right:
            *   /\
            * --------------
            * |            |
            * |            |
            * --------------
            * */
            boolean placeLeft = centerX < arrowX;
            /*
            top:
            *   /\
            * --------------
            * |            |
            * |            |
            * --------------
            bottom:
            * --------------
            * |            |
            * |            |
            * --------------
            *   V
            * */
            double arrowDimX = 10;
            double arrowDimY = 5;
            double roundStartX = placeLeft ? arrowX + arrowDimX / 2d + 10 - width : arrowX - arrowDimX / 2d - 10;
            double roundStartY = renderUpsideDown ? arrowY - arrowDimY - height : arrowY + arrowDimY;
            Matrix4f mat = stack.peek().getPositionMatrix();

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableCull();

            renderRoundedQuadInternal(mat, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f, roundStartX, roundStartY, roundStartX + width, roundStartY + height, 5, 20);
//            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tessellator t = Tessellator.getInstance();
            BufferBuilder bb = t.getBuffer();
            bb.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            if (renderUpsideDown) {
                bb.vertex(mat, (float) arrowX, (float) arrowY - .5f, 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
                bb.vertex(mat, (float) (arrowX - arrowDimX / 2f), (float) (arrowY - arrowDimY - .5), 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
                bb.vertex(mat, (float) (arrowX + arrowDimX / 2f), (float) (arrowY - arrowDimY - .5), 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
            } else {
                bb.vertex(mat, (float) arrowX, (float) arrowY + .5f, 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
                bb.vertex(mat, (float) (arrowX - arrowDimX / 2f), (float) (arrowY + arrowDimY + .5), 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
                bb.vertex(mat, (float) (arrowX + arrowDimX / 2f), (float) (arrowY + arrowDimY + .5), 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
            }
            t.draw();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            return new Vec2f((float) roundStartX, (float) roundStartY);
        }

        public static void doInitAnimationTransform(MatrixStack stack, double progress, double width, double height) {
            double originX = width / 2d;
            double originY = height / 2d;
            double translateX = originX * (1 - progress);
            double translateY = originY * (1 - progress);
            stack.translate(translateX, translateY, 0);
            stack.scale((float) progress, (float) progress, 1);
//            Matrix4f matrix = stack.peek().getPositionMatrix();
//            RenderSystem.enableBlend();
//            double x0 = maskX;
//            double y0 = maskY;
//            double x1 = maskX+width;
//            double y1 = maskY+height;
//            double z = 0;
////            RenderSystem.blendFunc(GL40C.GL_SRC_ALPHA, GL40C.GL_SRC_ALPHA);
//            RenderSystem.blendFuncSeparate(GL40.GL_ONE,GL40.GL_ONE,GL40C.GL_SRC_ALPHA,GL40C.GL_SRC_ALPHA);
//            RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
////            GL11.glAlphaFunc(GL11.GL_GREATER, 0.6f);
////            GL11.glEnable(GL11.GL_ALPHA_TEST);
//            RenderSystem.setShader(GameRenderer::getPositionColorShader);
//            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
//            bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).color(1f,0f,0f,(float) (1f-progress)).next();
//            bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).color(0f,1f,0f,(float) (1f-progress)).next();
//            bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).color(0f,0f,1f,(float) (1f-progress)).next();
//            bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).color(1f,1f,0f,(float) (1f-progress)).next();
//            bufferBuilder.end();
//            BufferRenderer.draw(bufferBuilder);
////            RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.SRC_ALPHA);
////            RenderSystem.defaultBlendFunc();
//            RenderSystem.blendFuncSeparate(GL40.GL_DST_COLOR,GL40.GL_SRC_COLOR,GL40C.GL_DST_ALPHA,GL40C.GL_SRC_ALPHA);
        }

        public static void beginScissor(MatrixStack stack, double x, double y, double endX, double endY) {
            Matrix4f matrix = stack.peek().getPositionMatrix();
            Vector4f coord = new Vector4f((float) x, (float) y, 0, 1);
            Vector4f end = new Vector4f((float) (endX), (float) (endY), 0, 1);
            coord.transform(matrix);
            end.transform(matrix);
            x = coord.getX();
            y = coord.getY();
            double width = end.getX() - x;
            double height = end.getY() - y;
            width = Math.max(0, width);
            height = Math.max(0, height);
            float d = (float) CoffeeClientMain.client.getWindow().getScaleFactor();
            int ay = (int) ((CoffeeClientMain.client.getWindow().getScaledHeight() - (y + height)) * d);
            RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
        }

        public static void endScissor() {
            RenderSystem.disableScissor();
        }

        public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
            double x1 = x0 + width;
            double y1 = y0 + height;
            double z = 0;
            renderTexturedQuad(matrices.peek()
                    .getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float) textureWidth, (u + (float) regionWidth) / (float) textureWidth, (v + 0.0F) / (float) textureHeight, (v + (float) regionHeight) / (float) textureHeight);
        }

        public static void renderLoadingSpinner(MatrixStack stack, Color c, double x, double y, double rad, double segments) {
            renderLoadingSpinner(stack, c, x, y, rad, 1, segments);
        }

        public static void renderLoadingSpinner(MatrixStack stack, float alpha, double x, double y, double rad, double width, double segments) {
            stack.push();
            stack.translate(x, y, 0);
            float rot = (System.currentTimeMillis() % 2000) / 2000f;
            stack.multiply(new Quaternion(0, 0, rot * 360f, true));
            segments = MathHelper.clamp(segments, 2, 90);
            RenderSystem.setShaderColor(1, 1, 1, 1);


            Matrix4f matrix = stack.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (double r = 0; r < 90; r += (90 / segments)) {
                double rad1 = Math.toRadians(r);
                double sin = Math.sin(rad1);
                double cos = Math.cos(rad1);
                double offX = sin * rad;
                double offY = cos * rad;
                float prog = (float) r / 360f;
                prog -= rot;
                prog %= 1;
                Color hsb = Color.getHSBColor(prog, .6f, 1f);
                float g = hsb.getRed() / 255f;
                float h = hsb.getGreen() / 255f;
                float k = hsb.getBlue() / 255f;
                bufferBuilder.vertex(matrix, (float) offX, (float) offY, 0).color(g, h, k, alpha).next();
                bufferBuilder.vertex(matrix, (float) (offX + sin * width), (float) (offY + cos * width), 0).color(g, h, k, alpha).next();

            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            stack.pop();
        }

        public static void renderLoadingSpinner(MatrixStack stack, Color c, double x, double y, double rad, double width, double segments) {
            stack.push();
            stack.translate(x, y, 0);
            stack.multiply(new Quaternion(0, 0, (System.currentTimeMillis() % 2000) / 2000f * 360f, true));
            segments = MathHelper.clamp(segments, 2, 90);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int color = c.getRGB();

            Matrix4f matrix = stack.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (double r = 0; r < 90; r += (90 / segments)) {
                double rad1 = Math.toRadians(r);
                double sin = Math.sin(rad1);
                double cos = Math.cos(rad1);
                double offX = sin * rad;
                double offY = cos * rad;
                bufferBuilder.vertex(matrix, (float) offX, (float) offY, 0).color(g, h, k, f).next();
                bufferBuilder.vertex(matrix, (float) (offX + sin * width), (float) (offY + cos * width), 0).color(g, h, k, f).next();

            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            stack.pop();
        }

        private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
            bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
        }

        public static void renderCircle(MatrixStack matrices, Color c, double originX, double originY, double rad, int segments) {
            segments = MathHelper.clamp(segments, 4, 360);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int color = c.getRGB();

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            for (int i = 0; i < 360; i += (360 / segments)) {
                double radians = Math.toRadians(i);
                double sin = Math.sin(radians) * rad;
                double cos = Math.cos(radians) * rad;
                bufferBuilder.vertex(matrix, (float) (originX + sin), (float) (originY + cos), 0).color(g, h, k, f).next();
            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void renderEntity(double x, double y, double size, float mouseX, float mouseY, LivingEntity entity, MatrixStack stack) {
            float f = (float) Math.atan(mouseX / 40.0F);
            float g = (float) Math.atan(mouseY / 40.0F);
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(x, y, 1050.0D);
            matrixStack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            stack.push();
            stack.translate(0.0D, 0.0D, 1000.0D);
            stack.scale((float) size, (float) size, (float) size);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
            quaternion.hamiltonProduct(quaternion2);
            stack.multiply(quaternion);
            float h = entity.bodyYaw;
            float i = entity.getYaw();
            float j = entity.getPitch();
            float k = entity.prevHeadYaw;
            float l = entity.headYaw;
            entity.bodyYaw = 180.0F + f * 20.0F;
            entity.setYaw(180.0F + f * 40.0F);
            entity.setPitch(-g * 20.0F);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            DiffuseLighting.method_34742();
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            //noinspection deprecation
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, immediate, 15728880));
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            entity.bodyYaw = h;
            entity.setYaw(i);
            entity.setPitch(j);
            entity.prevHeadYaw = k;
            entity.headYaw = l;
            matrixStack.pop();
            stack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();
        }

        public static boolean isOnScreen(Vec3d pos) {
            return pos != null && (pos.z > -1 && pos.z < 1);
        }

        public static Vec3d getScreenSpaceCoordinate(Vec3d pos, MatrixStack stack) {
            Camera camera = CoffeeClientMain.client.getEntityRenderDispatcher().camera;
            Matrix4f matrix = stack.peek().getPositionMatrix();
            double x = pos.x - camera.getPos().x;
            double y = pos.y - camera.getPos().y;
            double z = pos.z - camera.getPos().z;
            Vector4f vector4f = new Vector4f((float) x, (float) y, (float) z, 1.f);
            vector4f.transform(matrix);
            int displayHeight = CoffeeClientMain.client.getWindow().getHeight();
            Vector3D screenCoords = new Vector3D();
            int[] viewport = new int[4];
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            Matrix4x4 matrix4x4Proj = Matrix4x4.copyFromColumnMajor(RenderSystem.getProjectionMatrix());//no more joml :)
            Matrix4x4 matrix4x4Model = Matrix4x4.copyFromColumnMajor(RenderSystem.getModelViewMatrix());//but I do the math myself now :( (heck math)
            matrix4x4Proj.mul(matrix4x4Model).project(vector4f.getX(), vector4f.getY(), vector4f.getZ(), viewport, screenCoords);
            return new Vec3d(screenCoords.x / CoffeeClientMain.client.getWindow().getScaleFactor(), (displayHeight - screenCoords.y) / CoffeeClientMain.client.getWindow()
                    .getScaleFactor(), screenCoords.z);
        }

        public static void renderGradientLine(Color start, Color end, double x, double y, double x1, double y1) {
            float g = start.getRed() / 255f;
            float h = start.getGreen() / 255f;
            float k = start.getBlue() / 255f;
            float f = start.getAlpha() / 255f;
            float g1 = end.getRed() / 255f;
            float h1 = end.getGreen() / 255f;
            float k1 = end.getBlue() / 255f;
            float f1 = end.getAlpha() / 255f;
            Matrix4f m = R3D.getEmptyMatrixStack().peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
////            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
            bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g1, h1, k1, f1).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void renderQuad(MatrixStack matrices, Color c, double x1, double y1, double x2, double y2) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int color = c.getRGB();
            double j;
            if (x1 < x2) {
                j = x1;
                x1 = x2;
                x2 = j;
            }

            if (y1 < y2) {
                j = y1;
                y1 = y2;
                y2 = j;
            }
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
////            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

        public static void renderQuadGradient(MatrixStack matrices, Color c2, Color c1, double x1, double y1, double x2, double y2) {
            float r1 = c1.getRed() / 255f;
            float g1 = c1.getGreen() / 255f;
            float b1 = c1.getBlue() / 255f;
            float a1 = c1.getAlpha() / 255f;
            float r2 = c2.getRed() / 255f;
            float g2 = c2.getGreen() / 255f;
            float b2 = c2.getBlue() / 255f;
            float a2 = c2.getAlpha() / 255f;

            double j;

            if (x1 < x2) {
                j = x1;
                x1 = x2;
                x2 = j;
            }

            if (y1 < y2) {
                j = y1;
                y1 = y2;
                y2 = j;
            }
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(r1, g1, b1, a1).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r1, g1, b1, a1).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r2, g2, b2, a2).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(r2, g2, b2, a2).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

        }

        public static void renderQuad(Color c, double x1, double y1, double x2, double y2) {
            renderQuad(R3D.getEmptyMatrixStack(), c, x1, y1, x2, y2);
        }

        public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

            double toX1 = toX - rad;
            double toY1 = toY - rad;
            double fromX1 = fromX + rad;
            double fromY1 = fromY + rad;
            double[][] map = new double[][]{new double[]{toX1, toY1}, new double[]{toX1, fromY1}, new double[]{fromX1, fromY1}, new double[]{fromX1, toY1}};
            for (int i = 0; i < 4; i++) {
                double[] current = map[i];
                for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                    float rad1 = (float) Math.toRadians(r);
                    float sin = (float) (Math.sin(rad1) * rad);
                    float cos = (float) (Math.cos(rad1) * rad);
                    bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
                }
            }
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
        }

        public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double rad, double samples) {
//            RenderSystem.defaultBlendFunc();

            int color = c.getRGB();
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float f = (float) (color >> 24 & 255) / 255.0F;
            float g = (float) (color >> 16 & 255) / 255.0F;
            float h = (float) (color >> 8 & 255) / 255.0F;
            float k = (float) (color & 255) / 255.0F;
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, samples);

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        public static void renderLine(MatrixStack stack, Color c, double x, double y, double x1, double y1) {
            float g = c.getRed() / 255f;
            float h = c.getGreen() / 255f;
            float k = c.getBlue() / 255f;
            float f = c.getAlpha() / 255f;
            Matrix4f m = stack.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
//            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(m, (float) x, (float) y, 0f).color(g, h, k, f).next();
            bufferBuilder.vertex(m, (float) x1, (float) y1, 0f).color(g, h, k, f).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }

    }

    public static class Util {

        public static int lerp(int o, int i, double p) {
            return (int) Math.floor(i + (o - i) * MathHelper.clamp(p, 0, 1));
        }

        public static double lerp(double i, double o, double p) {
            return (i + (o - i) * MathHelper.clamp(p, 0, 1));
        }

        public static Color lerp(Color a, Color b, double c) {
            return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c), lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
        }

        /**
         * @param original       the original color
         * @param redOverwrite   the new red (or -1 for original)
         * @param greenOverwrite the new green (or -1 for original)
         * @param blueOverwrite  the new blue (or -1 for original)
         * @param alphaOverwrite the new alpha (or -1 for original)
         * @return the modified color
         */
        public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
            return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
        }


    }

}
