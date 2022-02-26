package me.x150.coffee.feature.gui.screen;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.FastTickable;
import me.x150.coffee.feature.gui.widget.RoundButton;
import me.x150.coffee.helper.ManagerThread;
import me.x150.coffee.helper.font.FontRenderers;
import me.x150.coffee.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.coffee.helper.render.Renderer;
import me.x150.coffee.helper.util.Transitions;
import me.x150.coffee.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;

public class StartScreen extends ClientScreen implements FastTickable {
    public static StartScreen currentInstance;
    public String status = "Connecting...";
    public String desc = "Connecting to the main server";
    public String footer = "";
    public boolean renderGoButton = false;
    ClientFontRenderer cfr = FontRenderers.getCustomSize(30);
    boolean shouldBeginStarting = false;
    RoundButton go;
    double opacity = 1;
    double goButtonYOffset = 35;
    double goButtonYOffsetRender = 35;

    public StartScreen() {
        currentInstance = this;
    }

    @Override
    protected void init() {
        if (CoffeeClientMain.sman == null) {
            CoffeeClientMain.sman = new ManagerThread();
            CoffeeClientMain.sman.start();
            Thread managerWatcherThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10_000);
                    } catch (Exception ignored) {
                    }
                    if (CoffeeClientMain.sman.needRestart.get()) {
                        System.out.println("Restarting because big man asked for it");
                        CoffeeClientMain.sman.clear();
                        CoffeeClientMain.sman = new ManagerThread();
                        CoffeeClientMain.sman.start();
                    }
                }
            });
            managerWatcherThread.start();
        }
        HomeScreen.instance().init(client, width, height);
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        width = CoffeeClientMain.client.getWindow().getScaledWidth();
        height = CoffeeClientMain.client.getWindow().getScaledHeight();
        if (shouldBeginStarting) {
            HomeScreen.instance().renderInternal(stack, mouseX, mouseY, delta);
            if (opacity == 0) {
                client.setScreen(HomeScreen.instance());
            }
        }
        Renderer.R2D.renderQuad(stack, new Color(0, 0, 0, (int) (opacity * 255)), 0, 0, width, height);
        List<String> statusList = List.of(Utils.splitLinesToWidth(desc, 300, FontRenderers.getRenderer()));

        double modalHeight = cfr.getFontHeight() + 5 + FontRenderers.getRenderer().getMarginHeight() * statusList.size();
        cfr.drawCenteredString(stack, status, width / 2d, height / 2d - modalHeight / 2d, 1, 1, 1, (float) opacity);
        double yOffset = cfr.getFontHeight() + 5;
        for (String s : statusList) {
            FontRenderers.getRenderer().drawCenteredString(stack, s, width / 2d, height / 2d - modalHeight / 2d + yOffset, 1, 1, 1, (float) opacity);
            yOffset += FontRenderers.getRenderer().getMarginHeight();
        }
        FontRenderers.getRenderer().drawCenteredString(stack, footer, width / 2d, height / 2d - modalHeight / 2d + yOffset, 1, 1, 1, (float) opacity);
        if (go == null) {
            go = new RoundButton(RoundButton.STANDARD, width / 2d - 120 / 2d, height - 10 - 20, 120, 20, "Continue", () -> {
                shouldBeginStarting = true;
                this.go.setEnabled(false);
            });
        }
        go.setY(height - 10 - 20 + goButtonYOffsetRender);
        go.render(stack, mouseX, mouseY, delta);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (go != null) go.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onFastTick() {
        if (shouldBeginStarting) {
            opacity -= 0.01;
            opacity = MathHelper.clamp(opacity, 0, 1);
        }
        goButtonYOffsetRender = Transitions.transition(goButtonYOffsetRender, goButtonYOffset, 30, 0);
        if (renderGoButton && !shouldBeginStarting) goButtonYOffset = 0;
        else if (shouldBeginStarting) goButtonYOffset = 30;
        if (go != null) go.onFastTick();
        HomeScreen.instance().onFastTick();
    }
}
