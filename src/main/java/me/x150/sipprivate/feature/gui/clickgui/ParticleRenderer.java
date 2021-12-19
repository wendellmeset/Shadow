package me.x150.sipprivate.feature.gui.clickgui;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ParticleRenderer {
    static Color DYING = new Color(255, 255, 255, 0); // it goes gradient so you can still see the white
    List<Particle> particles = new ArrayList<>();
    int            pc;

    public ParticleRenderer(int pc) {
        this.pc = pc;
        for (int i = 0; i < pc; i++) {
            addParticle();
        }
    }

    void addParticle() {
        Particle n = new Particle();
        n.x = Math.random() * SipoverPrivate.client.getWindow().getScaledWidth();
        n.y = SipoverPrivate.client.getWindow().getScaledHeight() + 10;
        n.velY = -(Math.random() * 2 + 1);
        n.decline = MathHelper.lerp(Math.random(), 0.1, 0.6);
        particles.add(n);
    }

    public void tick() {
        for (Particle particle : particles) {
            particle.move();
        }
        particles.removeIf(Particle::isDead);
        while (particles.size() < pc) {
            addParticle();
        }
    }

    public void render(MatrixStack stack) {
        for (Particle particle : particles) {
            particle.render(stack);
        }
    }

    static class Particle {
        long   rotSpeed = (long) MathHelper.lerp(Math.random(), 3000, 10000);
        double x        = 0, y = 0, size = 10, decline = 0.1;
        double velX = 0, velY = 0;
        boolean spinsReverse = Math.random() > .5;

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
            stack.multiply(new Quaternion(0, 0, (System.currentTimeMillis() % rotSpeed) / ((float) rotSpeed) * 360f * (spinsReverse ? -1 : 1), true));
            Renderer.R2D.fill(stack, Renderer.Util.lerp(theme.getAccent(), DYING, size / 10d), -size, -size, size, size);
            stack.pop();
        }

        boolean isDead() {
            return size <= 0;
        }
    }
}
