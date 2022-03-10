package net.shadow.client.feature.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.clickgui.theme.Theme;
import net.shadow.client.feature.gui.clickgui.theme.ThemeManager;
import net.shadow.client.helper.render.Renderer;
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
        n.x = ShadowMain.client.getWindow().getScaledWidth()*Math.random();
        n.y = ShadowMain.client.getWindow().getScaledHeight()*Math.random();
        n.velY = (Math.random()-.5)/4;
        n.velX = (Math.random()-.5)/4;
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
            particle.render(particles, stack);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    static class Particle {
        double velX = 0;
        double x = 0;
        double y = 0;
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
        long origLife = (long) (20*MathHelper.lerp(Math.random(),4,6));
        long life = origLife;
        void move() {
            life--;
            life = Math.max(0, life);
//            size -= decline;
//            size = Math.max(0, size);
            x += velX;
            y += velY;
            double h = ShadowMain.client.getWindow().getScaledHeight();
            double w = ShadowMain.client.getWindow().getScaledWidth();
            if (x > w || x < 0) {
                velX *= -1;
            }
            if (y > h || y < 0) {
                velY *= -1;
            }
            x = MathHelper.clamp(x,0,w);
            y = MathHelper.clamp(y,0,h);
        }

        void render(List<Particle> others, MatrixStack stack) {
            long fadeTime = 40;
            long startDelta = Math.min(origLife-life,fadeTime);
            long endDelta = Math.min(life,fadeTime);
            long deltaOverall = Math.min(startDelta,endDelta);
            double pk = (deltaOverall/(double)fadeTime);
//            ShadowMain.client.textRenderer.draw(stack,pk+"",(float)x,(float)y,0xFFFFFF);
            Theme theme = ThemeManager.getMainTheme();
            stack.push();
//            stack.translate(x, y, 0);
//            stack.multiply(new Quaternion(0, (System.currentTimeMillis() % rotSpeed2) / ((float) rotSpeed2) * 360f * (spinsReverse2 ? -1 : 1), (System.currentTimeMillis() % rotSpeed) / ((float) rotSpeed) * 360f * (spinsReverse ? -1 : 1), true));
//            //            Renderer.R2D.fill(stack, Renderer.Util.lerp(theme.getAccent(), DYING, size / 10d), -size, -size, size, size);
//            renderOutline(new Vec3d(-size, -size, -size), new Vec3d(size, size, size).multiply(2), Renderer.Util.lerp(theme.getAccent(), DYING, size / 10d), stack);
            double maxDist = 100;
            for (Particle particle : others.stream().filter(particle -> particle != this).toList()) {
                double px = particle.x;
                double py = particle.y;
                double dist = Math.sqrt(Math.pow((px-this.x), 2)+Math.pow((py-this.y),2));
                if (dist < maxDist) {
                    long sd1 = Math.min(particle.origLife-particle.life,fadeTime);
                    long ed1 = Math.min(particle.life,fadeTime);
                    long do1 = Math.min(sd1,ed1);
                    double pk1 = (do1/(double)fadeTime);
                    double p = 1-(dist/maxDist);
                    p = Math.min(p, Math.min(pk1, pk));
                    Renderer.R2D.renderLine(stack,Renderer.Util.lerp(theme.getAccent(),DYING,p),this.x,this.y,px,py);
                }
            }
            stack.pop();
        }

        boolean isDead() {
            return life == 0;
        }
    }
}
