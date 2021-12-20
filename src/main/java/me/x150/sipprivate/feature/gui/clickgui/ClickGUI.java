package me.x150.sipprivate.feature.gui.clickgui;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.CategoryDisplay;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.feature.gui.clickgui.theme.impl.SipoverV1;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.util.Transitions;
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
    public static       Theme    theme    = new SipoverV1();
    List<Element>    elements = new ArrayList<>();
    ParticleRenderer real     = new ParticleRenderer(100);
    String           desc     = null;
    double           descX, descY;
    double scroll         = 0;
    double trackedScroll = 0;
    double introAnimation = 0;
    boolean closing = false;
    private ClickGUI() {
        super(Text.of(""));
        initElements();
        Events.registerEventHandler(EventType.HUD_RENDER, event -> {
            if (this.real.particles.isEmpty() || !closing) return;
            this.real.render(Renderer.R3D.getEmptyMatrixStack());
        });
    }

    @Override protected void init() {
        closing = false;
        introAnimation = 0;
//        this.real.particles.clear();
        this.real.shouldAdd = true;
    }

    @Override public void onClose() {
        closing = true;
        this.real.shouldAdd = false;
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 10;
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
    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;

    }
    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (closing && introAnimation == 0) {
            client.setScreen(null);
            return;
        }
        if (!closing) real.render(matrices);
        matrices.push();
        matrices.translate(0, -easeInOutQuint(1-introAnimation)*height, 0);
        matrices.translate(0, -trackedScroll, 0);
        mouseY += trackedScroll;
        List<Element> rev = new ArrayList<>(elements);
        Collections.reverse(rev);
        for (Element element : rev) {
            element.render(matrices, mouseX, mouseY, trackedScroll);
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
        mouseY += trackedScroll;
        for (Element element : elements) {
            if (element.clicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (Element element : elements) {
            element.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseY += trackedScroll;
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
        double d = 0.03;
        if (closing) d *= -1;
        introAnimation += d;
        introAnimation = MathHelper.clamp(introAnimation, 0, 1);
        trackedScroll = Transitions.transition(trackedScroll, scroll, 7, 0);
        for (Element element : elements) {
            element.tickAnim();
        }
    }
}
