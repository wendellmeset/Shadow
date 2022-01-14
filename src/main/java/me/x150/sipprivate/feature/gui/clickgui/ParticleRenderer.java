package me.x150.sipprivate.feature.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ParticleRenderer {
    static final Color DYING = new Color(255, 255, 255, 0); // it goes gradient so you can still see the white
    public final List<Particle> particles = new ArrayList<>();
    final int pc;
    public boolean shouldAdd = true;
    long lastTick = System.currentTimeMillis();

    public ParticleRenderer(int pc) {
        this.pc = pc;
        for (int i = 0; i < pc; i++) {
            addParticle();
        }
    }

    void addParticle() {
        if (!shouldAdd) {
            return;
        }
        Particle n = new Particle();
        n.x = Math.random() * CoffeeClientMain.client.getWindow().getScaledWidth();
        n.y = -10;
        n.velY = (Math.random() + 1);
        n.decline = MathHelper.lerp(Math.random(), 0.05, 0.2);
        particles.add(n);
    }

    private void tick() {
        lastTick = System.currentTimeMillis();
        for (Particle particle : particles) {
            particle.move();
        }
        particles.removeIf(Particle::isDead);
    }

    public void render(MatrixStack stack) {
        long timeDiffSinceLastTick = System.currentTimeMillis() - lastTick;
        int iter = (int) Math.floor(timeDiffSinceLastTick / 20d);
        for (int i = 0; i < iter; i++) {
            tick();
        }
        if (particles.size() < this.pc) {
            addParticle();
        }
        for (Particle particle : particles) {
            particle.render(stack);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    static class Particle {
        final long rotSpeed = (long) MathHelper.lerp(Math.random(), 3000, 10000);
        final long rotSpeed2 = (long) MathHelper.lerp(Math.random(), 3000, 10000);
        final double velX = 0;
        final boolean spinsReverse = Math.random() > .5;
        final boolean spinsReverse2 = Math.random() > .5;
        double x = 0, y = 0, size = 10, decline = 0.1;
        double velY = 0;

        public static BufferBuilder renderPrepare(Color color) {
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            float alpha = color.getAlpha() / 255f;
            RenderSystem.setShader(GameRenderer::getPositionShader);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            RenderSystem.setShaderColor(red, green, blue, alpha);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
            return buffer;
        }

        public static void renderOutline(Vec3d start, Vec3d dimensions, Color color, MatrixStack stack) {
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            BufferBuilder buffer = renderPrepare(color);

            renderOutlineIntern(start, dimensions, stack, buffer);

            buffer.end();
            BufferRenderer.draw(buffer);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableBlend();
        }

        static void renderOutlineIntern(Vec3d start, Vec3d dimensions, MatrixStack stack, BufferBuilder buffer) {
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

        void move() {
            size -= decline;
            size = Math.max(0, size);
            x += velX;
            y += velY;
        }

        void render(MatrixStack stack) {
            Theme theme = ClickGUI.theme;
            stack.push();
            stack.translate(x, y, 0);
            stack.multiply(new Quaternion(0, (System.currentTimeMillis() % rotSpeed2) / ((float) rotSpeed2) * 360f * (spinsReverse2 ? -1 : 1), (System.currentTimeMillis() % rotSpeed) / ((float) rotSpeed) * 360f * (spinsReverse ? -1 : 1), true));
            //            Renderer.R2D.fill(stack, Renderer.Util.lerp(theme.getAccent(), DYING, size / 10d), -size, -size, size, size);
            renderOutline(new Vec3d(-size, -size, -size), new Vec3d(size, size, size).multiply(2), Renderer.Util.lerp(theme.getAccent(), DYING, size / 10d), stack);
            stack.pop();
        }

        boolean isDead() {
            return size <= 0;
        }
    }
}
