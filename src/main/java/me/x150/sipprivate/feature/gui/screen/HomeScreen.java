package me.x150.sipprivate.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.widget.RoundButton;
import me.x150.sipprivate.helper.Texture;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
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
import java.util.Objects;
import java.util.UUID;

public class HomeScreen extends ClientScreen implements FastTickable {
    static final double padding = 5;
    static final Texture background = new Texture("background.jpg");
    static final HttpClient downloader = HttpClient.newHttpClient();
    static boolean isDev = false;
    static String version = "unknown";
    static String changelog = "";
    private static HomeScreen instance;
    final ClientFontRenderer title = FontRenderers.getCustomNormal(40);
    final ClientFontRenderer smaller = FontRenderers.getCustomNormal(30);
    final ClientFontRenderer propFr = FontRenderers.getCustomNormal(22);
    final Texture currentAccountTexture = new Texture("dynamic/tex_currentaccount_home");
    boolean loaded = false;
    long initTime = System.currentTimeMillis();
    double prog = 0;
    boolean fadeOut = false;
    double initProg = 0;
    boolean currentAccountTextureLoaded = false;
    UUID previousChecked = null;

    private HomeScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
    }

    public static HomeScreen instance() {
        if (instance == null) instance = new HomeScreen();
        return instance;
    }

    void load() {
        loaded = true;
        try {
            File execF = new File(HomeScreen.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            isDev = execF.isDirectory();
            HomeScreen.version = IOUtils.toString(Objects.requireNonNull(HomeScreen.class.getClassLoader().getResourceAsStream("version.txt")), StandardCharsets.UTF_8);
            HomeScreen.changelog = IOUtils.toString(Objects.requireNonNull(HomeScreen.class.getClassLoader().getResourceAsStream("changelogLatest.txt")), StandardCharsets.UTF_8);
            updateCurrentAccount(this::complete);
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
        double centerWidgetsY = height - padding - 25;
        double rightPad = width - padding;
        Color bg = new Color(30, 30, 30);
        RoundButton single = new RoundButton(bg, rightPad - (padding + 60), centerWidgetsY, 60, 20, "Singleplayer", () -> CoffeeClientMain.client.setScreen(new SelectWorldScreen(this)));
        RoundButton multi = new RoundButton(bg, rightPad - (padding + 60) * 2, centerWidgetsY, 60, 20, "Multiplayer", () -> CoffeeClientMain.client.setScreen(new MultiplayerScreen(this)));
        RoundButton realms = new RoundButton(bg, rightPad - (padding + 60) * 3, centerWidgetsY, 60, 20, "Realms", () -> CoffeeClientMain.client.setScreen(new RealmsMainScreen(this)));
        RoundButton alts = new RoundButton(bg, rightPad - (padding + 60) * 4, centerWidgetsY, 60, 20, "Alts", () -> CoffeeClientMain.client.setScreen(AltManagerScreen.instance()));
        RoundButton settings = new RoundButton(bg, rightPad - (padding + 60) * 5, centerWidgetsY, 60, 20, "Options", () -> CoffeeClientMain.client.setScreen(new OptionsScreen(this, CoffeeClientMain.client.options)));
        RoundButton quit = new RoundButton(bg, rightPad - (padding + 60) * 5 - padding - 20, centerWidgetsY, 20, 20, "X", CoffeeClientMain.client::scheduleStop);
        addDrawableChild(single);
        addDrawableChild(multi);
        addDrawableChild(settings);
        addDrawableChild(alts);
        addDrawableChild(realms);
        addDrawableChild(quit);
    }

    @Override
    protected void init() {
        super.init();
        initTime = System.currentTimeMillis();
        initWidgets();
        if (loaded) updateCurrentAccount(() -> {
        }); // already loaded this instance, refresh on the fly
    }

    void complete() {
        fadeOut = true;
    }

    void updateCurrentAccount(Runnable callback) {
        UUID uid = CoffeeClientMain.client.getSession().getProfile().getId();
        if (previousChecked != null && previousChecked.equals(uid)) {
            callback.run();
            return;
        }
        previousChecked = uid;

        HttpRequest hr = HttpRequest.newBuilder().uri(URI.create("https://crafatar.com/avatars/" + uid + "?overlay")).header("User-Agent", "why").build();
        downloader.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                byte[] bytes = stream.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                NativeImage img = NativeImage.read(data);
                System.out.println(img);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(img);

                CoffeeClientMain.client.execute(() -> {
                    CoffeeClientMain.client.getTextureManager().registerTexture(currentAccountTexture, texture);
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

        stack.push();
        double ap = 1 - easeOutBack(MathHelper.clamp(initProg, .5, 1.5) - .5);
        double h = padding + propFr.getMarginHeight() + 2 + changelog.split("\n").length * FontRenderers.getNormal().getMarginHeight() + padding;
        double w = 100;
        for (String s : changelog.split("\n")) {
            w = Math.max(w, 10 + FontRenderers.getNormal().getStringWidth(s));
        }
        stack.translate(0, ap * -(padding + h + 1), 0);
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 170), padding, padding, padding + w + padding * 2, padding + h, 10, 14);
        propFr.drawString(stack, "Changelog", (float) (padding * 2f), (float) (padding * 2f), 0xFFFFFF, false);
        double yoff = padding * 2 + propFr.getMarginHeight() + 2;
        for (String s : changelog.split("\n")) {
            FontRenderers.getNormal().drawString(stack, s, (float) (padding * 2 + padding), (float) yoff, 0xAAAAAA, false);
            yoff += FontRenderers.getNormal().getMarginHeight();
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
        if (currentAccountTextureLoaded)
            Renderer.R2D.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
        else Renderer.R2D.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 8, 8, 8, 8, 64, 64);
        RenderSystem.defaultBlendFunc();
        String uuid = CoffeeClientMain.client.getSession().getUuid();
        double uuidWid = FontRenderers.getNormal().getStringWidth(uuid);
        double maxWid = 200 - texDim - padding * 3;
        if (uuidWid > maxWid) {
            double threeDotWidth = FontRenderers.getNormal().getStringWidth("...");
            uuid = FontRenderers.getNormal().trimStringToWidth(uuid, maxWid - 1 - threeDotWidth);
            uuid += "...";
        }
        AltManagerScreen.AltContainer.PropEntry[] props = new AltManagerScreen.AltContainer.PropEntry[]{new AltManagerScreen.AltContainer.PropEntry(CoffeeClientMain.client.getSession().getUsername(), FontRenderers.getCustomNormal(22), 0xFFFFFF),
                new AltManagerScreen.AltContainer.PropEntry(uuid, FontRenderers.getNormal(), 0xAAAAAA)};
        float propsOffset = (float) (fromY + padding);
        for (AltManagerScreen.AltContainer.PropEntry prop : props) {
            prop.cfr().drawString(stack, prop.name(), (float) (fromX + padding + texDim + padding), propsOffset, prop.color(), false);
            propsOffset += prop.cfr().getMarginHeight();
        }
        stack.pop();

        stack.push();
        double heiProg = 1 - easeOutBack(MathHelper.clamp(initProg, 0, 1));
        double totalHeight = 30;
        stack.translate(0, (totalHeight + padding) * heiProg, 0);
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 170), padding, height - padding - totalHeight, width - padding, height - padding, 10, 14);
        title.drawString(stack, "Coffee", 10f, (float) (height - padding - totalHeight / 2f - title.getMarginHeight() / 2f), 0xFFFFFF, false);
        double fw = title.getStringWidth("Coffee") + 5;
        smaller.drawString(stack, "version " + version + (isDev ? "-dev" : ""), (float) (10f + fw), (float) (height - padding - totalHeight / 2f - title.getMarginHeight() / 2f) + title.getMarginHeight() - smaller.getMarginHeight() - 1, 0xFFFFFF, false);
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
        if (CoffeeClientMain.client.getOverlay() == null && CoffeeClientMain.client.currentScreen == this && System.currentTimeMillis() - initTime > 1000 && !loaded)
            load();
        double delta = 10 / 600d;
        if (fadeOut) delta *= -1;
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
        if (prog != 0 || !fadeOut) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    double easeOutBack(double x) {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2);

    }
}
