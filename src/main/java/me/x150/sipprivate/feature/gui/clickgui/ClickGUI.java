package me.x150.sipprivate.feature.gui.clickgui;

import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.CategoryDisplay;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickGUI extends Screen implements FastTickable {
    List<Element>    elements = new ArrayList<>();
    ParticleRenderer real     = new ParticleRenderer(300);

    public static final ClickGUI instance = new ClickGUI();

    private ClickGUI() {
        super(Text.of(""));
        initElements();
    }

    void initElements() {
        for (ModuleType value : ModuleType.values()) {
            CategoryDisplay cd = new CategoryDisplay(0, 0, value);
            elements.add(cd);
        }
    }

    @Override public void tick() {
        real.tick();
        super.tick();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        real.render(matrices);
        List<Element> rev = new ArrayList<>(elements);
        Collections.reverse(rev);
        for (Element element : rev) {
            element.render(matrices);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : elements) {
            if (element.clicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Element element : elements) {
            element.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Element element : elements) {
            if (element.dragged(mouseX, mouseY, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element element : elements) {
            if (element.keyPressed(keyCode)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    @Override public void onFastTick() {
        for (Element element : elements) {
            element.tickAnim();
        }
    }
}
