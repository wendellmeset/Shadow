package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ClientScreen extends Screen {
    final int samples;

    public ClientScreen(int samples) {
        super(Text.of(""));
        this.samples = samples;
    }

    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        CoffeeClientMain.client.keyboard.setRepeatEvents(true);
        if (samples != -1) {
            if (!MSAAFramebuffer.framebufferInUse())
                MSAAFramebuffer.use(samples, () -> renderInternal(matrices, mouseX, mouseY, delta));
            else renderInternal(matrices, mouseX, mouseY, delta);
        } else {
            renderInternal(matrices, mouseX, mouseY, delta);
        }
    }
}
