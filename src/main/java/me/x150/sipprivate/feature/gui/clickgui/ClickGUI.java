package me.x150.sipprivate.feature.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;
import me.x150.sipprivate.feature.gui.clickgui.element.impl.CategoryDisplay;
import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;
import me.x150.sipprivate.feature.gui.clickgui.theme.impl.SipoverV1;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ClickGUI extends Screen implements FastTickable {
    public static final Theme theme = new SipoverV1();
    static Color idk = new Color(20, 20, 30, 255);
    private static ClickGUI instance;
    final List<Element> elements = new ArrayList<>();
    final ParticleRenderer real = new ParticleRenderer(100);
    public String searchTerm = "";
    String desc = null;
    double descX, descY;
    double scroll = 0;
    double trackedScroll = 0;
    double introAnimation = 0;
    boolean closing = false;

    private ClickGUI() {
        super(Text.of(""));
        initElements();
        Events.registerEventHandler(EventType.HUD_RENDER, event -> {
            if (this.real.particles.isEmpty() || !closing) {
                return;
            }
            this.real.render(Renderer.R3D.getEmptyMatrixStack());
        });
    }

    public static ClickGUI instance() {
        if (instance == null) {
            instance = new ClickGUI();
        }
        return instance;
    }

    @Override
    protected void init() {
        closing = false;
        introAnimation = 0;
        //        this.real.particles.clear();
        this.real.shouldAdd = true;
    }

    @Override
    public void onClose() {
        closing = true;
        this.real.shouldAdd = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 10;
        double bottomMost = 0;
        for (Element element : elements) {
            double y = element.getY() + element.getHeight();
            bottomMost = Math.max(bottomMost, y);
        }
        bottomMost -= height;
        bottomMost += 5; // leave 5 space between scroll end and deepest element
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
        double width = CoffeeClientMain.client.getWindow().getScaledWidth();
        double x = 5;
        double y = 5;
        double tallestInTheRoom = 0;
        for (ModuleType value : Arrays.stream(ModuleType.values()).sorted(Comparator.comparingLong(value -> -ModuleRegistry.getModules().stream().filter(module -> module.getModuleType() == value).count())).collect(Collectors.toList())) {
            CategoryDisplay cd = new CategoryDisplay(x, y, value);
            tallestInTheRoom = Math.max(tallestInTheRoom, cd.getHeight());
            x += cd.getWidth() + 5;
            if (x >= width) {
                y += tallestInTheRoom + 5;
                tallestInTheRoom = 0;
                x = 5;
            }
            elements.add(cd);
        }
    }

    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (closing && introAnimation == 0) {
            Objects.requireNonNull(client).setScreen(null);
            return;
        }
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> renderIntern(matrices, mouseX, mouseY, delta));
    }

    void renderIntern(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double wid = width / 2d;
        double hei = height / 2d;
        ClientFontRenderer bigAssFr = FontRenderers.getCustomNormal(70);
        double tx = wid - bigAssFr.getStringWidth(searchTerm) / 2d;
        double ty = hei - bigAssFr.getMarginHeight() / 2d;
        bigAssFr.drawString(matrices, searchTerm, (float) tx, (float) ty, 0x50FFFFFF, false);

        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        matrices.push();
        matrices.translate(0, 0, -20);
        if (!closing) {
            real.render(matrices);
        }
        matrices.pop();
        matrices.push();
        double intp = easeInOutQuint(introAnimation);
        matrices.translate((1 - intp) * width / 2, (1 - intp) * height / 2, 0);
        matrices.scale((float) intp, (float) intp, 1);
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

//            double width = FontRenderers.getNormal().getStringWidth(desc);
            double width = 0;
            List<String> text = Arrays.stream(desc.split("\n")).map(s -> s = s.trim()).collect(Collectors.toList());
            for (String s : text) {
                width = Math.max(width, FontRenderers.getNormal().getStringWidth(s));
            }
            if (descX + width > CoffeeClientMain.client.getWindow().getScaledWidth()) {
                descX -= (descX + width - CoffeeClientMain.client.getWindow().getScaledWidth()) + 4;
            }
            Vec2f root = Renderer.R2D.renderTooltip(matrices, descX, descY, width + 4, FontRenderers.getNormal().getMarginHeight() + 4, idk);
            float yOffset = 2;
            for (String s : text) {
                FontRenderers.getNormal().drawString(matrices, s, root.x + 1, root.y + yOffset, 0xFFFFFF, false);
                yOffset += FontRenderers.getNormal().getMarginHeight();
            }

            desc = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (Element element : new ArrayList<>(elements)) {
            if (element.clicked(mouseX, mouseY, button)) {
                elements.remove(element);
                elements.add(0, element); // put to front when clicked
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseY += trackedScroll;
        for (Element element : elements) {
            element.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseY += trackedScroll;
        for (Element element : elements) {
            if (element.dragged(mouseX, mouseY, deltaX, deltaY, button)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element element : elements) {
            if (element.keyPressed(keyCode, modifiers)) {
                return true;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && searchTerm.length() > 0) {
            searchTerm = searchTerm.substring(0, searchTerm.length() - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE && !searchTerm.isEmpty()) {
            searchTerm = "";
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onFastTick() {
        double d = 0.03;
        if (closing) {
            d *= -1;
        }
        introAnimation += d;
        introAnimation = MathHelper.clamp(introAnimation, 0, 1);
        trackedScroll = Transitions.transition(trackedScroll, scroll, 7, 0);
        for (Element element : elements) {
            element.tickAnim();
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Element element : elements) {
            if (element.charTyped(chr, modifiers)) return true;
        }
        searchTerm += chr;
        return false;
    }
}
