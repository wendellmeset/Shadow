package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class BindScreen extends Screen {
    final Module a;
    final ClientFontRenderer cfr = FontRenderers.getCustomNormal(30);
    final ClientFontRenderer smaller = FontRenderers.getCustomNormal(20);
    long closeAt = -1;

    public BindScreen(Module toBind) {
        super(Text.of(""));
        this.a = toBind;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        cfr.drawCenteredString(matrices, "Press any key", width / 2d, height / 2d - cfr.getMarginHeight(), 0xAAAAAA);
        String kn = a.keybind.getValue() > 0 ? GLFW.glfwGetKeyName((int) (a.keybind.getValue() + 0), GLFW.glfwGetKeyScancode((int) (a.keybind.getValue() + 0))) : "None";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    if (declaredField.getName().startsWith("GLFW_KEY_")) {
                        int a = (int) declaredField.get(null);
                        if (a == this.a.keybind.getValue()) {
                            String nb = declaredField.getName().substring("GLFW_KEY_".length());
                            kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                        }
                    }
                }
            } catch (Exception ignored) {
                kn = "unknown." + (int) (a.keybind.getValue() + 0);
            }
        }
        smaller.drawCenteredString(matrices, "Current bind: " + kn, width / 2d, height / 2d, 0xBBBBBB);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (closeAt != -1) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            keyCode = -1;
        }
        a.keybind.setValue((double) keyCode);
        closeAt = System.currentTimeMillis() + 500;
        return true;
    }

    @Override
    public void tick() {
        if (closeAt != -1 && closeAt < System.currentTimeMillis()) {
            onClose();
        }
        super.tick();
    }
}
