package net.shadow.client.feature.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.client.CoffeeClientMain;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.helper.Timer;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Transitions;
import org.apache.commons.compress.utils.Lists;

import java.awt.*;

public class TestScreen extends Screen implements FastTickable {
    final double[] data = new double[100];
    final double[] viewerData = new double[data.length];
    final Timer updater = new Timer();
    DoubleOptionSliderWidget goopy;
    DoubleOptionSliderWidget curve;
    double goopyV = 1d;
    double curveV = 1d;

    public TestScreen() {
        super(Text.of(""));

    }

    @Override
    protected void init() {
        DoubleOption dop = new DoubleOption("bruh", 1, 10, 0.01f, gameOptions -> 1d, (gameOptions, aDouble) -> goopyV = aDouble, (gameOptions, doubleOption) -> Text.of("goopy"));
        goopy = new DoubleOptionSliderWidget(CoffeeClientMain.client.options, 5, height - 30, 100, 20, dop, Lists.newArrayList());
        DoubleOption curve = new DoubleOption("curve", 1, 10, 0.01f, gameOptions -> 1d, (gameOptions, aDouble) -> curveV = aDouble, (gameOptions, doubleOption) -> Text.of("real"));
        this.curve = new DoubleOptionSliderWidget(CoffeeClientMain.client.options, 110, height - 30, 100, 20, curve, Lists.newArrayList());
        addDrawableChild(goopy);
        addDrawableChild(this.curve);
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);


        double v = 0;
        double x = 5;
        double lastX = x - 1;
        double lastY = height / 2d;
        double velocity = 0;
        for (double viewerDatum : viewerData) {
            for (int i = 0; i < 5; i++) {
                velocity += (viewerDatum - v) / curveV;
                velocity /= goopyV;
                v += velocity;
                Renderer.R2D.renderLine(matrices, Color.WHITE, x, height / 2d - v, lastX, lastY);
                lastX = x;
                lastY = height / 2d - v;

                x += 1;
            }

        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onFastTick() {
        if (updater.hasExpired(5000)) {
            updater.reset();
            for (int i = 0; i < data.length; i++) {
                data[i] = Math.random() * 100;
            }
        }
        for (int i = 0; i < viewerData.length; i++) {
            viewerData[i] = Transitions.transition(viewerData[i], data[i], 20, 0);
        }
    }
}
