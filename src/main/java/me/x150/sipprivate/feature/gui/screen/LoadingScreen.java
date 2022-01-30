package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingScreen extends ClientScreen implements FastTickable {
    static LoadingScreen INSTANCE = null;
    static Color GREEN = new Color(100, 255, 20);
    static Color RED = new Color(255, 50, 20);
    AtomicBoolean loaded = new AtomicBoolean(false);
    AtomicBoolean loadInProg = new AtomicBoolean(false);
    double progress = 0;
    double smoothProgress = 0;
    double opacity = 1;
    ClientFontRenderer title = FontRenderers.getCustomNormal(40);

    private LoadingScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
    }

    public static LoadingScreen instance() {
        if (INSTANCE == null) INSTANCE = new LoadingScreen();
        return INSTANCE;
    }

    @Override
    protected void init() {
        HomeScreen.instance().init(client, width, height);
        if (loaded.get() && opacity == 0.001) {
            client.setScreen(HomeScreen.instance());
        }
        super.init();
    }

    @Override
    public void onFastTick() {
        smoothProgress = Transitions.transition(smoothProgress, progress, 10, 0.0001);
        if (CoffeeClientMain.client.getOverlay() == null) {
            if (!loadInProg.get()) {
                load();
            }
        }
        if (loaded.get()) {
            opacity -= 0.01;
            opacity = MathHelper.clamp(opacity, 0.001, 1);
        }
        HomeScreen.instance().onFastTick();
    }

    void load() {
        loadInProg.set(true);
        new Thread(() -> {
            HttpClient downloader = HttpClient.newHttpClient();
            for (int i = 0; i < CoffeeClientMain.resources.size(); i++) {
                CoffeeClientMain.ResourceEntry resource = CoffeeClientMain.resources.get(i);
                CoffeeClientMain.log(Level.INFO, "Downloading " + resource.url());
                try {
                    HttpRequest hrq = HttpRequest.newBuilder()
                            .uri(URI.create(resource.url()))
                            .build();
                    HttpResponse<byte[]> b = downloader.send(hrq, HttpResponse.BodyHandlers.ofByteArray());
                    BufferedImage bi = ImageIO.read(new ByteArrayInputStream(b.body()));
                    Utils.registerBufferedImageTexture(resource.tex(), bi);
                    CoffeeClientMain.log(Level.INFO, "Downloaded " + resource.url());
                } catch (Exception ignored) {
                    CoffeeClientMain.log(Level.ERROR, "Failed to download " + resource.url());
                } finally {
                    progress = i / ((double) CoffeeClientMain.resources.size() - 1);
                }
            }
            loaded.set(true);
        }, "Loader").start();
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {

        if (loaded.get()) {
            HomeScreen.instance().renderInternal(stack, mouseX, mouseY, delta);
            if (opacity == 0.001) {
                this.client.setScreen(HomeScreen.instance());
                return;
            }
        }
        Renderer.R2D.renderQuad(stack, new Color(0, 0, 0, (float) opacity), 0, 0, width, height);
        String coffee = "Loading Coffee..";
        double pad = 5;
        double textWidth = title.getStringWidth(coffee) + 1;
        double textHeight = title.getMarginHeight();
        double centerY1 = height / 2d;
        double centerX = width / 2d;
        title.drawString(stack, coffee, centerX - textWidth / 2f, centerY1 - textHeight / 2d, new Color(1f, 1f, 1f, (float) opacity).getRGB());
        double maxWidth = 200;
        double rWidth = smoothProgress * maxWidth;
        double barHeight = 3;
        rWidth = Math.max(rWidth, barHeight);

        Color MID_END = Renderer.Util.lerp(GREEN, RED, smoothProgress);
        Renderer.R2D.renderRoundedQuad(stack, new Color(40, 40, 40, (int) (opacity * 255)), centerX - maxWidth / 2d, centerY1 + textHeight / 2d + pad, centerX + maxWidth / 2d, centerY1 + textHeight / 2d + pad + barHeight, barHeight / 2d, 10);
        Renderer.R2D.renderRoundedQuad(stack, Renderer.Util.modify(MID_END, -1, -1, -1, (int) (opacity * 255)), centerX - maxWidth / 2d, centerY1 + textHeight / 2d + pad, centerX - maxWidth / 2d + rWidth, centerY1 + textHeight / 2d + pad + barHeight, barHeight / 2d, 10);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }
}
