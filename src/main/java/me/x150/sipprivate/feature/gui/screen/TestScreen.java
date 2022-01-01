package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.Color;

public class TestScreen extends Screen {
    public TestScreen() {
        super(Text.of(""));
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            Renderer.R2D.renderRoundedQuad(matrices, Color.RED,50,50,100,100,10);
        });

        super.render(matrices, mouseX, mouseY, delta);
    }
}
