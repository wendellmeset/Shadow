/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.feature.gui.clickgui.ParticleRenderer;
import net.shadow.client.feature.gui.widget.RoundButton;
import net.shadow.client.helper.GameTexture;
import net.shadow.client.helper.Texture;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.font.adapter.FontAdapter;
import net.shadow.client.helper.render.MSAAFramebuffer;
import net.shadow.client.helper.render.Renderer;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40C;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class HomeScreen extends ClientScreen implements FastTickable {
    static final double padding = 5;
    static final Texture background = GameTexture.TEXTURE_BACKGROUND.getWhere();
    static final HttpClient downloader = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    static boolean isDev = false;
    static String version = "unknown";
    static String changelog = "";
    private static HomeScreen instance;
    final ParticleRenderer prend = new ParticleRenderer(300);
    final FontAdapter smaller = FontRenderers.getCustomSize(30);
    final FontAdapter propFr = FontRenderers.getCustomSize(22);
    final Texture currentAccountTexture = new Texture("dynamic/tex_currentaccount_home");
    final double widgetsWidth = 60;
    boolean loaded = false;
    long initTime = System.currentTimeMillis();
    double prog = 0;
    boolean fadeOut = false;
    double initProg = 0;
    boolean currentAccountTextureLoaded = false;
    UUID previousChecked = null;
    double widgetsHeight = 0;

    private HomeScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
    }

    public static HomeScreen instance() {
        if (instance == null) {
            instance = new HomeScreen();
        }
//        instance = new HomeScreen();
        return instance;
    }

    void load() {
        loaded = true;
        try {
            File execF = new File(HomeScreen.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            isDev = execF.isDirectory();
            HomeScreen.version = IOUtils.toString(Objects.requireNonNull(HomeScreen.class.getClassLoader().getResourceAsStream("version.txt")), StandardCharsets.UTF_8);
            HomeScreen.changelog = IOUtils.toString(Objects.requireNonNull(HomeScreen.class.getClassLoader().getResourceAsStream("changelogLatest.txt")), StandardCharsets.UTF_8);
//            System.out.println("updating acc");
            updateCurrentAccount(() -> {

            });
            complete();
        } catch (Exception e) {
            e.printStackTrace();
            complete();
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        clearChildren();
        initWidgets();
    }

    void initWidgets() {
        double widPad = 6;

        double widgetHeight = 20;

        List<Map.Entry<String, Runnable>> buttonsMap = new ArrayList<>();
        buttonsMap.add(new AbstractMap.SimpleEntry<>("Singleplayer", () -> ShadowMain.client.setScreen(new SelectWorldScreen(this))));
        buttonsMap.add(new AbstractMap.SimpleEntry<>("Multiplayer", () -> ShadowMain.client.setScreen(new MultiplayerScreen(this))));
        buttonsMap.add(new AbstractMap.SimpleEntry<>("Realms", () -> ShadowMain.client.setScreen(new RealmsMainScreen(this))));
        buttonsMap.add(new AbstractMap.SimpleEntry<>("Alts", () -> ShadowMain.client.setScreen(
                AltManagerScreen.instance()
//                new AddonManagerScreen()
        )));
        buttonsMap.add(new AbstractMap.SimpleEntry<>("Settings", () -> ShadowMain.client.setScreen(new OptionsScreen(this, ShadowMain.client.options))));
        widgetsHeight = buttonsMap.size() * (widgetHeight + widPad) - widPad;
        double xOffset = -innerBottomPadding(); // dont question it
        Color bg = new Color(30, 30, 30);
        for (Map.Entry<String, Runnable> stringRunnableEntry : buttonsMap) {
            xOffset += widgetsWidth + innerBottomPadding();
            RoundButton rb = new RoundButton(bg, width - bottomPadding() - innerBottomPadding() - xOffset, height - bottomPadding() - innerBottomPadding() - widgetHeight, widgetsWidth, widgetHeight, stringRunnableEntry.getKey(), stringRunnableEntry.getValue());
            addDrawableChild(rb);
        }
    }

    double innerBottomPadding() {
        return 3d;
    }

    double bottomPadding() {
        return 5d;
    }

    @Override
    protected void init() {
        super.init();
        initTime = System.currentTimeMillis();
        initWidgets();
        if (loaded) {
            updateCurrentAccount(() -> {
            }); // already loaded this instance, refresh on the fly
        }
    }

    void complete() {
        fadeOut = true;
    }

    void updateCurrentAccount(Runnable callback) {
        UUID uid = ShadowMain.client.getSession().getProfile().getId();
        if (previousChecked != null && previousChecked.equals(uid)) {
            callback.run();
            return;
        }
        previousChecked = uid;

        HttpRequest hr = HttpRequest.newBuilder().uri(URI.create("https://crafatar.com/avatars/" + uid + "?overlay")).header("User-Agent", "why").timeout(Duration.ofSeconds(5)).build();
        downloader.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                byte[] bytes = stream.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                NativeImage img = NativeImage.read(data);
//                System.out.println(img);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(img);

                ShadowMain.client.execute(() -> {
                    ShadowMain.client.getTextureManager().registerTexture(currentAccountTexture, texture);
                    currentAccountTextureLoaded = true;
                    callback.run();
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.run();
            }
        });
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {

        double initProg = this.initProg * 2;

        Renderer.R2D.renderQuad(stack, new Color(20, 20, 20), 0, 0, width, height);

        RenderSystem.setShaderTexture(0, background);
        Renderer.R2D.renderTexture(stack, 0, 0, width, height, 0, 0, width, height, width, height);
        RenderSystem.defaultBlendFunc();
        prend.render(stack);
        stack.push();
        double ap = 1 - easeOutBack(MathHelper.clamp(initProg, .5, 1.5) - .5);
        double h = padding + propFr.getMarginHeight() + 2 + changelog.split("\n").length * FontRenderers.getRenderer().getMarginHeight() + padding;
        double w = 100;
        for (String s : changelog.split("\n")) {
            w = Math.max(w, 10 + FontRenderers.getRenderer().getStringWidth(s.strip()));
        }
        stack.translate(0, ap * -(padding + h + 1), 0);

        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 170), padding, padding, padding + w + padding * 2, padding + h, 10, 14);

        propFr.drawString(stack, "Changelog", (float) (padding * 2f), (float) (padding * 2f), 0xFFFFFF, false);
        double yoff = padding * 2 + propFr.getMarginHeight() + 2;
        for (String s : changelog.split("\n")) {
            FontRenderers.getRenderer().drawString(stack, s, (float) (padding * 2 + padding), (float) yoff, 0xAAAAAA, false);
            yoff += FontRenderers.getRenderer().getMarginHeight();
        }
        stack.pop();

        stack.push();
        double widRHeight = 50 + padding * 2;
        double ap1 = 1 - easeOutBack(MathHelper.clamp(initProg, 1, 2) - 1);
        stack.translate(0, ap1 * -(padding + widRHeight + 1), 0);

        double fromX = width - (200 + padding);
        double toX = width - padding;
        double fromY;
        double toY;
        toY = padding + widRHeight;
        fromY = toY - widRHeight;
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 170), fromX, fromY, toX, toY, 10, 10);
        double texDim = widRHeight - padding * 2;

        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Renderer.R2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, fromX + padding, fromY + padding, fromX + padding + texDim, fromY + padding + texDim, 6, 10);

        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
        RenderSystem.setShaderTexture(0, currentAccountTextureLoaded ? currentAccountTexture : DefaultSkinHelper.getTexture());
        if (currentAccountTextureLoaded) {
            Renderer.R2D.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
        } else {
            Renderer.R2D.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 8, 8, 8, 8, 64, 64);
        }
        RenderSystem.defaultBlendFunc();
        String uuid = ShadowMain.client.getSession().getUuid();
        double uuidWid = FontRenderers.getRenderer().getStringWidth(uuid);
        double maxWid = 200 - texDim - padding * 3;
        if (uuidWid > maxWid) {
            double threeDotWidth = FontRenderers.getRenderer().getStringWidth("...");
            uuid = FontRenderers.getRenderer().trimStringToWidth(uuid, maxWid - 1 - threeDotWidth);
            uuid += "...";
        }
        AltManagerScreen.AltContainer.PropEntry[] props = new AltManagerScreen.AltContainer.PropEntry[]{
                new AltManagerScreen.AltContainer.PropEntry(ShadowMain.client.getSession().getUsername(), FontRenderers.getCustomSize(22), 0xFFFFFF),
                new AltManagerScreen.AltContainer.PropEntry(uuid, FontRenderers.getRenderer(), 0xAAAAAA)};
        float propsOffset = (float) (fromY + padding);
        for (AltManagerScreen.AltContainer.PropEntry prop : props) {
            prop.cfr().drawString(stack, prop.name(), (float) (fromX + padding + texDim + padding), propsOffset, prop.color(), false);
            propsOffset += prop.cfr().getMarginHeight();
        }
        stack.pop();
        double lowerBarHeight = innerBottomPadding() + 20 + innerBottomPadding();
        double iconDimHeight = lowerBarHeight - innerBottomPadding() * 2d;


        double originalIconWidth = 782;
        double originalIconHeight = 1000;

        double del1 = iconDimHeight / originalIconHeight;
        double iconDimWidth = originalIconWidth * del1;

        double fw = iconDimWidth + 5;
        stack.push();
        double heiProg = 1 - easeOutBack(MathHelper.clamp(initProg, 0, 1));
        double totalHeight = 30;
        String verstring = "v" + version + (isDev ? "-dev" : "");
        stack.translate(0, (totalHeight + padding) * heiProg, 0);

        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 170), bottomPadding(), height - bottomPadding() - lowerBarHeight, width - bottomPadding(), height - bottomPadding(), 5, 20);

        RenderSystem.setShaderTexture(0, GameTexture.TEXTURE_ICON.getWhere());
        Renderer.R2D.renderTexture(stack, bottomPadding() + innerBottomPadding(), height - bottomPadding() - innerBottomPadding() - iconDimHeight, iconDimWidth, iconDimHeight, 0, 0, iconDimWidth, iconDimHeight, iconDimWidth, iconDimHeight);
        smaller.drawString(stack, verstring, (float) (bottomPadding() + innerBottomPadding() + fw), height - bottomPadding() - lowerBarHeight / 2d - smaller.getMarginHeight() / 2d, 0xFFFFFF);
        super.renderInternal(stack, mouseX, mouseY, delta); // render bottom row widgets
        stack.pop();


        double spinnerProg = prog;
        spinnerProg = MathHelper.clamp(spinnerProg, 0, 1); // fucking floating point precision istg
        float fadeProg = fadeOut ? (float) prog : 1f;
        fadeProg = MathHelper.clamp(fadeProg, 0, 1);
        Renderer.R2D.renderQuad(stack, new Color(0, 0, 0, fadeProg * 0.8f), 0, 0, width, height);
        Renderer.R2D.renderLoadingSpinner(stack, (float) spinnerProg, width - 25, height - 25, 20, 1d, 20);
    }

    @Override
    public void onFastTick() {
        if (ShadowMain.client.getOverlay() == null && ShadowMain.client.currentScreen == this && System.currentTimeMillis() - initTime > 1000 && !loaded) {
            load();
        }
        double delta = 10 / 600d;
        if (fadeOut) {
            delta *= -1;
        }
        prog += delta;
        prog = MathHelper.clamp(prog, 0, 1);

        if (loaded && prog == 0 && fadeOut) {
            double d = 0.01;
            initProg += d;
            initProg = MathHelper.clamp(initProg, 0, 1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (prog != 0 || !fadeOut) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);

    }
}
