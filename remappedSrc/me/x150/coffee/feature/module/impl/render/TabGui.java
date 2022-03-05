package me.x150.coffee.feature.module.impl.render;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleRegistry;
import me.x150.coffee.feature.module.ModuleType;
import me.x150.coffee.helper.Timer;
import me.x150.coffee.helper.event.EventType;
import me.x150.coffee.helper.event.Events;
import me.x150.coffee.helper.event.events.KeyboardEvent;
import me.x150.coffee.helper.font.FontRenderers;
import me.x150.coffee.helper.render.ClipStack;
import me.x150.coffee.helper.render.Rectangle;
import me.x150.coffee.helper.render.Renderer;
import me.x150.coffee.helper.util.Transitions;
import me.x150.coffee.mixin.IDebugHudAccessor;
import me.x150.coffee.mixin.IInGameHudAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;

import static me.x150.coffee.feature.module.impl.render.Hud.getTitleFr;

public class TabGui extends Module {
    int            scrollerIndex   = 0;
    double         scrollerRenderY = 0;
    double         scrollerY       = 0;
    List<GuiEntry> entries         = new ArrayList<>();
    List<GuiEntry> root            = new ArrayList<>();
    double         scroll          = 0;
    double         smoothScroll    = 0;
    Timer          updater         = new Timer();

    public TabGui() {
        super("TabGui", "Renders a small module manager top left", ModuleType.RENDER);
        Events.registerEventHandler(EventType.KEYBOARD, event -> {
            if (!this.isEnabled()) {
                return;
            }
            KeyboardEvent ke = (KeyboardEvent) event;
            if (ke.getType() == 0) {
                return;
            }
            switch (ke.getKeycode()) {
                case GLFW.GLFW_KEY_DOWN -> scrollerIndex++;
                case GLFW.GLFW_KEY_UP -> scrollerIndex--;
                case GLFW.GLFW_KEY_RIGHT -> {
                    scrollerIndex = makeSureInBounds(scrollerIndex);
                    entries.get(scrollerIndex).activated();
                }
                case GLFW.GLFW_KEY_LEFT -> {
                    scrollerIndex = makeSureInBounds(scrollerIndex);
                    entries.get(scrollerIndex).back();
                }
            }
        });
    }

    int makeSureInBounds(int index) {
        index %= entries.size();
        if (index < 0) {
            index = entries.size() + index;
        }
        return index;
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        entries.clear();
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {
        double innerPad = 5;
        double heightOffsetLeft = 6 + Math.max(getTitleFr().getMarginHeight(), FontRenderers.getRenderer().getMarginHeight()) + 2 + innerPad;
        if (CoffeeClientMain.client.options.debugEnabled) {
            double heightAccordingToMc = 9;
            List<String> lt = ((IDebugHudAccessor) ((IInGameHudAccessor) CoffeeClientMain.client.inGameHud).getDebugHud()).callGetLeftText();
            heightOffsetLeft += 2 + heightAccordingToMc * (lt.size() + 3);
        }
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        ms.push();
        ms.translate(innerPad, heightOffsetLeft, 0);
        drawInternal(ms);
        ms.pop();
    }

    void drawInternal(MatrixStack stack) {

        double innerPadding = 2;
        double scrollerWidth = 1.5;
        double width = ModuleRegistry.getModules().stream().map(value -> FontRenderers.getRenderer().getStringWidth(value.getName())).max(Comparator.comparingDouble(value -> value))
                .orElse(60f) + scrollerWidth * 6 + innerPadding * 2;
        double cellHeight = FontRenderers.getRenderer().getMarginHeight() + 1;
        double maxHeight = cellHeight * 16;

        if (entries.isEmpty()) {
            for (ModuleType value : ModuleType.values()) {
                entries.add(new GuiEntry(() -> {
                    entries.clear();
                    for (Module module : ModuleRegistry.getModules()) {
                        if (module.getModuleType() == value) {
                            GuiEntry ge = new GuiEntry(() -> module.setEnabled(!module.isEnabled()), () -> entries = new ArrayList<>(root), module::isEnabled, module.getName(), cellHeight, width - scrollerWidth * 6);
                            entries.add(ge);
                        }

                    }
                }, () -> {

                }, () -> false, value.getName(), cellHeight, width - scrollerWidth * 6));
            }
            root = new ArrayList<>(entries);
        }
        double contentHeight = entries.size() * cellHeight;
        double viewerHeight = Math.min(maxHeight, contentHeight) + innerPadding * 2;
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20), 0, 0, width, viewerHeight, 3, 20);
        scrollerIndex = makeSureInBounds(scrollerIndex);
        scrollerY = scrollerIndex * cellHeight;
        double sc = (double) scrollerIndex / (entries.size() - 1);
        scroll = sc * (contentHeight + innerPadding * 2d - viewerHeight);
        stack.push();
        ClipStack.globalInstance.addWindow(stack, new Rectangle(scrollerWidth * 3, 0, width - innerPadding, viewerHeight - innerPadding));
        //Renderer.R2D.beginScissor(stack, scrollerWidth * 3, 0, width - innerPadding, viewerHeight - innerPadding);
        stack.translate(0, -smoothScroll, 0);


        double lastEnabledStackHeight = 0;
        double lastEnabledStackY = 0;
        double yOff = innerPadding;
        List<Runnable> renderCalls = new ArrayList<>();
        for (GuiEntry ge : entries) {
            if (ge.isEnabled()) {
                if (lastEnabledStackHeight == 0) {
                    lastEnabledStackY = yOff;
                }
                lastEnabledStackHeight += cellHeight;
            } else {
                if (lastEnabledStackHeight != 0) {
                    double finalLastEnabledStackY = lastEnabledStackY;
                    double finalLastEnabledStackHeight = lastEnabledStackHeight;
                    renderCalls.add(() -> {
                        Renderer.R2D.renderRoundedQuad(stack, new Color(40, 40, 40), scrollerWidth * 3, finalLastEnabledStackY, width - scrollerWidth * 3, finalLastEnabledStackY + finalLastEnabledStackHeight, 3, 20);
                    });

                }
                lastEnabledStackHeight = 0;
                lastEnabledStackY = 0;
            }
            yOff += cellHeight;
        }
        double finalLastEnabledStackY = lastEnabledStackY;
        double finalLastEnabledStackHeight = lastEnabledStackHeight;
        if (lastEnabledStackY != 0) {
            renderCalls.add(() -> {
                Renderer.R2D.renderRoundedQuad(stack, new Color(40, 40, 40), scrollerWidth * 3, finalLastEnabledStackY, width - scrollerWidth * 3, finalLastEnabledStackY + finalLastEnabledStackHeight, 3, 20);
            });
        }
        for (Runnable renderCall : renderCalls) {
            renderCall.run();
        }
        stack.push();
        stack.translate(scrollerWidth * 3, innerPadding, 0);
        for (int i = 0; i < entries.size(); i++) {
            GuiEntry ge = entries.get(i);
            boolean selected = i == scrollerIndex;

            ge.animate(selected ? 1 : 0);
            ge.render(stack);
            stack.translate(0, cellHeight, 0);
        }

        stack.pop();

        ClipStack.globalInstance.popWindow();
        //Renderer.R2D.endScissor();

        Renderer.R2D.renderRoundedQuad(stack, Color.WHITE, 2, innerPadding + scrollerRenderY + .5, 2 + scrollerWidth, innerPadding + scrollerRenderY + cellHeight - .5, scrollerWidth / 2d, 20);
        stack.pop();
    }

    @Override public void onFastTick() {
        scrollerRenderY = Transitions.transition(scrollerRenderY, scrollerY, 7, 0.00001);
        smoothScroll = Transitions.transition(smoothScroll, scroll, 7, 0.00001);
        if (updater.hasExpired(3000)) {
            updater.reset();
        }
        for (GuiEntry entry : entries) {
            entry.fastTick();
        }
    }

    static class GuiEntry {
        double   animation     = 0;
        double   animationGoal = 0;
        Runnable r, back;
        BooleanSupplier bs;
        String          name;
        double          h, w;

        public GuiEntry(Runnable r, Runnable back, BooleanSupplier isEnabled, String name, double height, double width) {
            this.r = r;
            this.bs = isEnabled;
            this.back = back;
            this.name = name;
            this.h = height;
            this.w = width;
        }

        boolean isEnabled() {
            return bs.getAsBoolean();
        }

        void fastTick() {
            this.animation = Transitions.transition(animation, animationGoal, 7, 0.00001);
        }

        void animate(double d) {
            this.animationGoal = d;
        }

        void render(MatrixStack stack) {
            //            Renderer.R2D.renderQuad(stack,Color.BLUE,0,0,w,h);
            FontRenderers.getRenderer().drawString(stack, name, MathHelper.lerp(animation, 2, w / 2d - FontRenderers.getRenderer().getStringWidth(name) / 2d), (h - FontRenderers.getRenderer()
                    .getMarginHeight()) / 2d, 0xFFFFFF);
        }

        void activated() {
            r.run();
        }

        void back() {
            back.run();
        }
    }
}
