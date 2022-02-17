package me.x150.coffee.feature.gui.screen;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.FastTickable;
import me.x150.coffee.helper.font.FontRenderers;
import me.x150.coffee.helper.render.Renderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.*;

public class TestScreen extends Screen implements FastTickable {
    ParticleSimulator ps = new ParticleSimulator();
    List<Long> recorded = new ArrayList<>();

    public TestScreen() {
        super(Text.of(""));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        Renderer.R2D.renderQuad(matrices, new Color(0, 0, 0, 10), 0, 0, width, height);
//        ps.render(matrices);
        renderBackground(matrices);
        String testString = "ABCDEFG".repeat(512);
        long start = System.nanoTime();
        FontRenderers.getRenderer().drawString(matrices, testString, 5, 5, 0xFFFFFF);
        long end = System.nanoTime();
        FontRenderers.getRenderer().drawString(matrices, "Draw: " + (end - start), 5, FontRenderers.getRenderer().getMarginHeight() + 2, 0xFFFFFF);
        recorded.add(end - start);
        long avg = recorded.stream().reduce(Long::sum).orElse(0L) / recorded.size();
        FontRenderers.getRenderer().drawString(matrices, "Avg 100: " + avg, 5, FontRenderers.getRenderer().getMarginHeight() * 2, 0xFFFFFF);
        while (recorded.size() > 1000) recorded.remove(0);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onFastTick() {
        ps.tick();
    }

    @Override
    public void tick() {
//        ps.tick();
        super.tick();
    }

    class ParticleSimulator {
        List<Particle> p = new ArrayList<>();
        Map<Particle, List<Particle>> partners = new HashMap<>();

        public ParticleSimulator() {
            for (int i = 0; i < (300); i++) {
                Particle p = new Particle();
                p.velX = Math.random() / 5;
                p.velY = Math.random() / 5;
                this.p.add(p);
            }
        }

        public void tick() {
            double width = CoffeeClientMain.client.getWindow().getScaledWidth();
            double height = CoffeeClientMain.client.getWindow().getScaledHeight();
            for (Particle particle : p) {
                particle.tick();
                if (particle.x > width || particle.x < 0) {
                    particle.velX *= -1;
                }
                if (particle.y > height || particle.y < 0) {
                    particle.velY *= -1;
                }
            }
        }

        double distance(double sx, double sy, double dx, double dy) {
            double distX = dx - sx;
            double distY = dy - sy;
            return Math.sqrt(distX * distX + distY * distY);
        }

        public void render(MatrixStack stack) {
            List<Particle> p = new ArrayList<>(this.p);
            for (Particle particle : p) {
                if (!partners.containsKey(particle)) partners.put(particle, new ArrayList<>());
                else partners.get(particle).clear();
            }
            for (Particle particle : p) {
                List<Particle> nearest = p.stream().filter(value -> value != particle/* && distance(value.x,value.y,particle.x,particle.y) < 100*/).sorted(Comparator.comparingDouble(value -> distance(value.x, value.y, particle.x, particle.y))).toList();
                for (Particle particle1 : nearest) {
                    if (partners.get(particle1).size() >= 2) {
//                        System.out.println("ren");
                        continue;
                    }
                    if (partners.get(particle).size() >= 2) {
                        break;
                    }
                    double dist = distance(particle1.x, particle1.y, particle.x, particle.y);
                    double inverted = (100 - dist) / 100;
                    if (inverted < 0 || inverted > 1) continue; // out of dist
                    Renderer.R2D.renderLine(stack, new Color(255, 50, 50, (int) Math.floor(inverted * 255)), particle.x, particle.y, particle1.x, particle1.y);
                    partners.get(particle).add(particle1);
                    partners.get(particle1).add(particle);
                }


            }
        }

        class Particle {
            double x, y, velX, velY;

            public void tick() {
                x += velX;
                y += velY;
            }
        }
    }
}
