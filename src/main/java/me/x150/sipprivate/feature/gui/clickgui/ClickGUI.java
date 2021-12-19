package me.x150.sipprivate.feature.gui.clickgui;

import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.CategoryDisplay;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.ModuleDisplay;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickGUI extends Screen {
    List<Element> elements = new ArrayList<>();
    void initElements() {
        for (ModuleType value : ModuleType.values()) {
            CategoryDisplay cd = new CategoryDisplay(0,0,value);
            elements.add(cd);
        }
    }
    public ClickGUI() {
        super(Text.of(""));
        initElements();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        List<Element> rev = new ArrayList<>(elements);
        Collections.reverse(rev);
        for (Element element : rev) {
            element.render(matrices);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : elements) {
            if (element.clicked(mouseX, mouseY, button)) return true;
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
            if (element.dragged(mouseX,mouseY,deltaX,deltaY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element element : elements) {
            if (element.keyPressed(keyCode)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}
