package me.x150.sipprivate.feature.gui.clickgui;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.CategoryDisplay;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.feature.gui.clickgui.theme.impl.SipoverV1;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickGUI extends Screen implements FastTickable {
    public static final ClickGUI instance = new ClickGUI();
    public static Theme theme = new SipoverV1();
    List<Element>    elements = new ArrayList<>();
    ParticleRenderer real     = new ParticleRenderer(100);
    String desc = null;
    double descX, descY;
    double scroll = 0;

    private ClickGUI() {
        super(Text.of(""));
        initElements();
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll += amount * 10;
        double bottomMost = 0;
        for (Element element : elements) {
            double y = element.getY() + element.getHeight();
            bottomMost = Math.max(bottomMost, y);
        }
        bottomMost -= height;
        bottomMost = Math.max(0, bottomMost);
        scroll = MathHelper.clamp(scroll, 0, bottomMost);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void renderDescription(double x, double y, String text) {
        desc = text;
        descX = x;
        descY = y;
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
        matrices.push();
        matrices.translate(0, -scroll, 0);
        mouseY += scroll;
        List<Element> rev = new ArrayList<>(elements);
        Collections.reverse(rev);
        for (Element element : rev) {
            element.render(matrices, mouseX, mouseY, scroll);
        }
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
        if (desc != null) {
            double width = FontRenderers.getNormal().getStringWidth(desc);
            if (descX + width > SipoverPrivate.client.getWindow().getScaledWidth()) {
                descX -= (descX + width - SipoverPrivate.client.getWindow().getScaledWidth()) + 4;
            }
            Renderer.R2D.fill(new Color(20, 20, 30, 200), descX - 1, descY, descX + width + 3, descY + FontRenderers.getNormal().getMarginHeight() + 1);
            FontRenderers.getNormal().drawString(Renderer.R3D.getEmptyMatrixStack(), desc, descX, descY, 0xFFFFFF);
            desc = null;
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += scroll;
        for (Element element : elements) {
            if (element.clicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseY += scroll;
        for (Element element : elements) {
            element.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseY += scroll;
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
