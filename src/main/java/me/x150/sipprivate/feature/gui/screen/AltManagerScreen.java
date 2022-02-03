package me.x150.sipprivate.feature.gui.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.authlib.login.mojang.MinecraftAuthenticator;
import me.x150.authlib.login.mojang.MinecraftToken;
import me.x150.authlib.login.mojang.profile.MinecraftProfile;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.widget.RoundButton;
import me.x150.sipprivate.feature.gui.widget.RoundTextFieldWidget;
import me.x150.sipprivate.helper.Texture;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import me.x150.sipprivate.helper.util.Utils;
import me.x150.sipprivate.mixin.IMinecraftClientAccessor;
import me.x150.sipprivate.mixin.SessionAccessor;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
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
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AltManagerScreen extends ClientScreen implements FastTickable {
    public static final Map<UUID, Texture> texCache = new HashMap<>();
    static final File ALTS_FILE = new File(CoffeeClientMain.BASE, "alts.sip");
    static final String TOP_NOTE = """
            // DO NOT SHARE THIS FILE
            // This file contains sensitive information about your accounts
            // Unless you REALLY KNOW WHAT YOU ARE DOING, DO NOT SEND THIS TO ANYONE
            """;
    static final HttpClient downloader = HttpClient.newHttpClient();
    static Color bg = new Color(20, 20, 20);
    static Color pillColor = new Color(40, 40, 40, 100);
    static Color widgetColor = new Color(40, 40, 40);
    static Color backgroundOverlay = new Color(0, 0, 0, 130);
    static Color overlayBackground = new Color(30, 30, 30);
    private static AltManagerScreen instance = null;
    final List<AltContainer> alts = new ArrayList<>();
    final double leftWidth = 200;
    final ClientFontRenderer titleSmall = FontRenderers.getCustomNormal(30);
    final ClientFontRenderer title = FontRenderers.getCustomNormal(40);
    final AtomicBoolean isLoggingIn = new AtomicBoolean(false);
    AltContainer selectedAlt;
    ThemedButton add, exit, remove, login, session, censorMail;
    RoundTextFieldWidget search;
    boolean censorEmail = true;
    double scroll = 0;
    double scrollSmooth = 0;
    Texture currentAccountTexture = new Texture("dynamic/currentaccount");
    boolean currentAccountTextureLoaded = false;

    private AltManagerScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
        loadAlts();
        updateCurrentAccount();
    }

    public static AltManagerScreen instance() {
        if (instance == null) {
            instance = new AltManagerScreen();
        }
        return instance;
    }

    public List<AltContainer> getAlts() {
        return alts.stream().filter(altContainer -> altContainer.storage.cachedName.toLowerCase().startsWith(search.get().toLowerCase()) || altContainer.storage.email.toLowerCase().startsWith(search.get().toLowerCase())).collect(Collectors.toList());
    }

    void saveAlts() {
        CoffeeClientMain.log(Level.INFO, "Saving alts");
        JsonArray root = new JsonArray();
        for (AltContainer alt1 : alts) {
            AltStorage alt = alt1.storage;
            JsonObject current = new JsonObject();
            current.addProperty("email", alt.email);
            current.addProperty("password", alt.password);
            current.addProperty("type", alt.type.name());
            current.addProperty("cachedUsername", alt.cachedName);
            current.addProperty("cachedUUID", alt.cachedUuid != null ? alt.cachedUuid.toString() : null);
            current.addProperty("valid", alt.valid);
            root.add(current);
        }
        try {
            FileUtils.write(ALTS_FILE, TOP_NOTE + "\n" + root, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            CoffeeClientMain.log(Level.ERROR, "Failed to write alts file");
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        saveAlts();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    void loadAlts() {
        CoffeeClientMain.log(Level.INFO, "Loading alts");

        if (!ALTS_FILE.isFile()) {
            ALTS_FILE.delete();
        }
        if (!ALTS_FILE.exists()) {
            CoffeeClientMain.log(Level.INFO, "Skipping alt loading because file doesn't exist");
            return;
        }
        try {
            String contents = FileUtils.readFileToString(ALTS_FILE, StandardCharsets.UTF_8);
            JsonArray ja = JsonParser.parseString(contents).getAsJsonArray();
            for (JsonElement jsonElement : ja) {
                JsonObject jo = jsonElement.getAsJsonObject();
                try {
                    AltStorage container = new AltStorage(jo.get("cachedUsername").getAsString(), jo.get("email").getAsString(), jo.get("password").getAsString(), UUID.fromString(jo.get("cachedUUID").getAsString()), AddScreenOverlay.AccountType.valueOf(jo.get("type").getAsString()));
                    container.valid = !jo.has("valid") || jo.get("valid").getAsBoolean();
                    AltContainer ac = new AltContainer(0, 0, 0, container);
                    ac.renderY = ac.renderX = -1;
                    alts.add(ac);
                } catch (Exception ignored) {

                }

            }
        } catch (Exception ignored) {
            CoffeeClientMain.log(Level.ERROR, "Failed to read alts file - corrupted?");
        }
    }

    double getPadding() {
        return 7;
    }

    double getHeaderHeight() {
        return 10 + getPadding() + title.getMarginHeight();
    }

    public void setSelectedAlt(AltContainer selectedAlt) {
        this.selectedAlt = selectedAlt;

    }

    void toggleCensor() {
        censorEmail = !censorEmail;
        censorMail.text = censorEmail ? "Show email" : "Hide email";
    }

    @Override
    protected void init() {
        search = new RoundTextFieldWidget(width - 200 - 5 - 100 - 5 - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 200, 20, "Search", 10);
        addDrawableChild(search);
        censorMail = new ThemedButton(width - 100 - 5 - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 100, 20, "Show email", this::toggleCensor);
        add = new ThemedButton(width - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 60, 20, "Add", () -> client.setScreen(new AddScreenOverlay(this)));
        exit = new ThemedButton(width - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 20, 20, "X", this::onClose);

        double padding = 5;
        double widRHeight = 64 + padding * 2;
        double toX = width - getPadding();
        double fromY = getHeaderHeight();
        double toY = fromY + widRHeight;
        double fromX = width - (leftWidth + getPadding());
        double texDim = widRHeight - padding * 2;
        double buttonWidth = (toX - (fromX + texDim + padding * 2)) / 2d - padding / 4d;
        login = new ThemedButton(fromX + texDim + padding * 2, toY - 20 - padding, buttonWidth - padding, 20, "Login", this::login);
        remove = new ThemedButton(fromX + texDim + padding * 2 + buttonWidth + padding / 2d, toY - 20 - padding, buttonWidth - padding, 20, "Remove", this::remove);

        toY = height - getPadding();
        buttonWidth = toX - fromX - padding * 3 - texDim;
        session = new ThemedButton(fromX + texDim + padding * 2, toY - 20 - padding, buttonWidth, 20, "Session", () -> {
            Objects.requireNonNull(client).setScreen(new SessionEditor(this, CoffeeClientMain.client.getSession())); // this is not a session stealer
        });
    }

    void updateCurrentAccount() {
        UUID uid = CoffeeClientMain.client.getSession().getProfile().getId();

        if (texCache.containsKey(uid)) {
            this.currentAccountTexture = texCache.get(uid);
            currentAccountTextureLoaded = true;
            return;
        }

        HttpRequest hr = HttpRequest.newBuilder().uri(URI.create("https://crafatar.com/avatars/" + uid + "?overlay")).header("User-Agent", "why").build();
        downloader.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                byte[] bytes = stream.toByteArray();

                ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                data.flip();
                NativeImage img = NativeImage.read(data);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(img);

                CoffeeClientMain.client.execute(() -> {
                    CoffeeClientMain.client.getTextureManager().registerTexture(currentAccountTexture, texture);
                    currentAccountTextureLoaded = true;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void login() {
        if (this.selectedAlt == null) return;
        isLoggingIn.set(true);
        new Thread(() -> {
            this.selectedAlt.login();
            isLoggingIn.set(false);
            if (!this.selectedAlt.storage.valid) {
                return;
            }
            Session newSession = new Session(selectedAlt.storage.cachedName, selectedAlt.storage.cachedUuid.toString(), selectedAlt.storage.accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
            ((IMinecraftClientAccessor) CoffeeClientMain.client).setSession(newSession);
            updateCurrentAccount();
        }).start();
    }

    void remove() {
        if (this.selectedAlt == null) return;
        alts.remove(this.selectedAlt);
        this.selectedAlt = null;
    }

    @Override
    public void onFastTick() {
        for (AltContainer alt : getAlts()) {
            alt.tickAnim();
        }
        censorMail.tickAnim();
        add.tickAnim();
        exit.tickAnim();
        remove.tickAnim();
        login.tickAnim();
        session.tickAnim();
        scrollSmooth = Transitions.transition(scrollSmooth, scroll, 7, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= amount * 10;
        double max = 0;
        for (AltContainer alt : getAlts()) {
            max = Math.max(max, alt.y + alt.getHeight());
        }
        max -= height;
        max += getPadding();
        max = Math.max(0, max);
        scroll = MathHelper.clamp(scroll, 0, max);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Renderer.R2D.renderQuad(stack, bg, 0, 0, width, height);
        title.drawString(stack, "Coffee", 10, 10, 0xFFFFFF, false);
        titleSmall.drawString(stack, "Alt manager", 10 + title.getStringWidth("Coffee") + 5, 10 + title.getMarginHeight() - titleSmall.getMarginHeight() - 1, 0xFFFFFF, false);
        censorMail.render(stack, mouseX, mouseY);
        add.render(stack, mouseX, mouseY);
        exit.render(stack, mouseX, mouseY);

        Renderer.R2D.beginScissor(stack, getPadding(), getHeaderHeight(), getPadding() + (width - (getPadding() + leftWidth + getPadding() * 2)), height);
        stack.push();
        stack.translate(0, -scrollSmooth, 0);
        mouseY += scrollSmooth;
        double x = getPadding();
        double y = getHeaderHeight();
        double wid = width - (getPadding() + leftWidth + getPadding() * 2);
        for (AltContainer alt : getAlts()) {
            alt.x = x;
            alt.y = y;
            alt.width = wid;
            if (alt.renderX == -1) alt.renderX = -alt.width;
            if (alt.renderY == -1) alt.renderY = alt.y;
            alt.render(stack, mouseX, mouseY);
            y += alt.getHeight() + getPadding();
        }
        stack.pop();
        Renderer.R2D.endScissor();

        double padding = 5;
        double widRHeight = 64 + padding * 2;

        double fromX = width - (leftWidth + getPadding());
        double toX = width - getPadding();
        double fromY = getHeaderHeight();
        double toY = fromY + widRHeight;

        Renderer.R2D.renderRoundedQuad(stack, pillColor, fromX, fromY, toX, toY, 10, 10);
        if (selectedAlt != null) {
            double texDim = widRHeight - padding * 2;

            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Renderer.R2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, fromX + padding, fromY + padding, fromX + padding + texDim, fromY + padding + texDim, 6, 10);

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, selectedAlt.tex);
            Renderer.R2D.renderTexture(stack, fromX + padding, fromY + padding, texDim, texDim, 0, 0, 64, 64, 64, 64);
            RenderSystem.defaultBlendFunc();
            String mail = this.selectedAlt.storage.email;
            String[] mailPart = mail.split("@");
            String domain = mailPart[mailPart.length - 1];
            String mailN = String.join("@", Arrays.copyOfRange(mailPart, 0, mailPart.length - 1));
            if (censorEmail) {
                mailN = "*".repeat(mailN.length());
            }
            mail = mailN + "@" + domain;
            AltContainer.PropEntry[] props = new AltContainer.PropEntry[]{new AltContainer.PropEntry(selectedAlt.storage.cachedName, FontRenderers.getCustomNormal(22), 0xFFFFFF),
                    new AltContainer.PropEntry(mail, FontRenderers.getNormal(), 0xAAAAAA),
                    new AltContainer.PropEntry("Type: " + selectedAlt.storage.type.s, FontRenderers.getNormal(), 0xAAAAAA)};
            float propsOffset = (float) (fromY + padding);
            for (AltContainer.PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (fromX + padding + texDim + padding), propsOffset, prop.color, false);
                propsOffset += prop.cfr.getMarginHeight();
            }
            remove.render(stack, mouseX, mouseY);
            login.render(stack, mouseX, mouseY);
        }

        toY = height - getPadding();
        fromY = toY - widRHeight;
        Renderer.R2D.renderRoundedQuad(stack, pillColor, fromX, fromY, toX, toY, 10, 10);
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
        double maxWid = leftWidth - texDim - padding * 3;
        if (uuidWid > maxWid) {
            double threeDotWidth = FontRenderers.getNormal().getStringWidth("...");
            uuid = FontRenderers.getNormal().trimStringToWidth(uuid, maxWid - 1 - threeDotWidth);
            uuid += "...";
        }
        AltContainer.PropEntry[] props = new AltContainer.PropEntry[]{new AltContainer.PropEntry(CoffeeClientMain.client.getSession().getUsername(), FontRenderers.getCustomNormal(22), 0xFFFFFF),
                new AltContainer.PropEntry(uuid, FontRenderers.getNormal(), 0xAAAAAA)};
        float propsOffset = (float) (fromY + padding);
        for (AltContainer.PropEntry prop : props) {
            prop.cfr.drawString(stack, prop.name, (float) (fromX + padding + texDim + padding), propsOffset, prop.color, false);
            propsOffset += prop.cfr.getMarginHeight();
        }
        session.render(stack, mouseX, mouseY);

        super.renderInternal(stack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        censorMail.clicked(mouseX, mouseY);
        if (isLoggingIn.get()) return false;
        add.clicked(mouseX, mouseY);
        exit.clicked(mouseX, mouseY);
        if (this.selectedAlt != null) {
            login.clicked(mouseX, mouseY);
            remove.clicked(mouseX, mouseY);
        }
        session.clicked(mouseX, mouseY);
        for (AltContainer alt : getAlts()) {
            alt.clicked(mouseX, mouseY + scrollSmooth);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    static class ThemedButton {
        final Runnable onPress;
        final double width;
        final double height;
        String text;
        double x;
        double y;
        double animProgress = 0;
        boolean isHovered = false;
        boolean enabled = true;


        public ThemedButton(double x, double y, double w, double h, String t, Runnable a) {
            this.onPress = a;
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.text = t;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void tickAnim() {
            double d = 0.04;
            if (!isHovered) {
                d *= -1;
            }
            animProgress += d;
            animProgress = MathHelper.clamp(animProgress, 0, 1);

        }

        double easeInOutQuint(double x) {
            return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
        }

        boolean inBounds(double cx, double cy) {
            return cx >= x && cx < x + width && cy >= y && cy < y + height;
        }

        public void render(MatrixStack matrices, double mx, double my) {
            isHovered = inBounds(mx, my) && isEnabled();
            matrices.push();
            matrices.translate(x + width / 2d, y + height / 2d, 0);
            float animProgress = (float) easeInOutQuint(this.animProgress);
            matrices.scale(MathHelper.lerp(animProgress, 1f, 0.95f), MathHelper.lerp(animProgress, 1f, 0.95f), 1f);
            double originX = -width / 2d;
            double originY = -height / 2d;
            Renderer.R2D.renderRoundedQuad(matrices, widgetColor, originX, originY, width / 2d, height / 2d, 10, 10);
            FontRenderers.getNormal().drawString(matrices, text, -(FontRenderers.getNormal().getStringWidth(text) + 2) / 2f, -FontRenderers.getNormal().getMarginHeight() / 2f, isEnabled() ? 0xFFFFFF : 0xAAAAAA, false);
            matrices.pop();
        }

        public void clicked(double mx, double my) {
            if (inBounds(mx, my) && isEnabled()) {
                onPress.run();
            }
        }
    }

    static class AltStorage {
        final String email;
        final String password;
        final AddScreenOverlay.AccountType type;
        String cachedName;
        String accessToken;
        UUID cachedUuid;
        boolean valid = true;
        boolean didLogin = false;

        public AltStorage(String n, String e, String p, UUID u, AddScreenOverlay.AccountType type) {
            this.cachedName = n;
            this.email = e;
            this.password = p;
            this.cachedUuid = u;
            this.type = type;
        }
    }

    static class SessionEditor extends ClientScreen {
        static final double widgetWid = 300;
        static double widgetHei = 0;
        final Session session;
        final ClientScreen parent;
        final double padding = 5;
        final ClientFontRenderer title = FontRenderers.getCustomNormal(40);
        RoundTextFieldWidget access, name, uuid;
        RoundButton save;

        public SessionEditor(ClientScreen parent, Session s) {
            super(MSAAFramebuffer.MAX_SAMPLES);
            this.session = s;
            this.parent = parent;
        }

        @Override
        protected void init() {
            RoundButton exit = new RoundButton(widgetColor, width - 20 - 5, 5, 20, 20, "X", () -> Objects.requireNonNull(client).setScreen(parent));
            addDrawableChild(exit);
            double y = height / 2d - widgetHei / 2d + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding;
            RoundTextFieldWidget accessToken = new RoundTextFieldWidget(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Access token", 10);
            accessToken.setText(session.getAccessToken());
            y += accessToken.getHeight() + padding;
            RoundTextFieldWidget username = new RoundTextFieldWidget(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Username", 10);
            username.setText(session.getUsername());
            y += username.getHeight() + padding;
            RoundTextFieldWidget uuid = new RoundTextFieldWidget(width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "UUID", 10);
            uuid.setText(session.getUuid());
            y += uuid.getHeight() + padding;
            RoundButton save = new RoundButton(widgetColor, width / 2d - (widgetWid - padding * 2) / 2d, y, widgetWid - padding * 2, 20, "Save", () -> {
                SessionAccessor sa = (SessionAccessor) session;
                sa.setUsername(username.get());
                sa.setAccessToken(accessToken.get());
                sa.setUuid(uuid.get());
                Objects.requireNonNull(client).setScreen(parent);
            });
            y += 20 + padding;
            this.save = save;
            access = accessToken;
            name = username;
            this.uuid = uuid;
            addDrawableChild(save);
            addDrawableChild(access);
            addDrawableChild(name);
            addDrawableChild(uuid);
            widgetHei = y - (height / 2d - widgetHei / 2d);
            super.init();
        }

        @Override
        public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
            if (parent != null) {
                parent.renderInternal(stack, mouseX, mouseY, delta);
            }

            double y = height / 2d - widgetHei / 2d + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding;
            access.setY(y);
            y += access.getHeight() + padding;
            name.setY(y);
            y += name.getHeight() + padding;
            uuid.setY(y);
            y += uuid.getHeight() + padding;
            save.setY(y);
            y += 20 + padding;
            widgetHei = y - (height / 2d - widgetHei / 2d);


            save.setEnabled(!name.get().isEmpty() && !uuid.get().isEmpty()); // enable when both name and uuid are set
            Renderer.R2D.renderQuad(stack, backgroundOverlay, 0, 0, width, height);


            double centerX = width / 2d;
            double centerY = height / 2d;
            Renderer.R2D.renderRoundedQuad(stack, overlayBackground, centerX - widgetWid / 2d, centerY - widgetHei / 2d, centerX + widgetWid / 2d, centerY + widgetHei / 2d, 10, 10);
            stack.push();

            double originX = width / 2d - widgetWid / 2d;
            double originY = height / 2d - widgetHei / 2d;
            title.drawString(stack, "Edit session", (float) (originX + padding), (float) (originY + padding), 0xFFFFFF, false);
            FontRenderers.getNormal().drawString(stack, "Edit your user session here", (float) (originX + padding), (float) (originY + padding + title.getMarginHeight()), 0xAAAAAA, false);
            stack.pop();
            super.renderInternal(stack, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Element child : children()) {
                child.mouseClicked(-1, -1, button);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    class AddScreenOverlay extends ClientScreen implements FastTickable {
        static final double widgetWid = 200;
        static int accountTypeI = 0;
        static double widgetHei = 0;
        final List<ThemedButton> buttons = new ArrayList<>();
        final ClientScreen parent;
        final double padding = 5;
        final ClientFontRenderer title = FontRenderers.getCustomNormal(40);
        RoundTextFieldWidget email;
        RoundTextFieldWidget passwd;
        ThemedButton type;
        ThemedButton add;

        public AddScreenOverlay(ClientScreen parent) {
            super(MSAAFramebuffer.MAX_SAMPLES);
            this.parent = parent;
        }

        @Override
        protected void init() {
            ThemedButton exit = new ThemedButton(width - 20 - 5, 5, 20, 20, "X", () -> Objects.requireNonNull(client).setScreen(parent));
            buttons.add(exit);
            email = new RoundTextFieldWidget(width / 2d - (widgetWid - padding * 2) / 2d, height / 2d - widgetHei / 2d + padding, widgetWid - padding * 2, 20, "E-Mail or username", 10);
            passwd = new RoundTextFieldWidget(width / 2d - (widgetWid - padding * 2) / 2d, height / 2d - widgetHei / 2d + padding * 2 + 20, widgetWid - padding * 2, 20, "Password", 10);
            type = new ThemedButton(0, 0, widgetWid / 2d - padding * 1.5, 20, "Type: " + AccountType.values()[accountTypeI].s, this::cycle);
            add = new ThemedButton(0, 0, widgetWid / 2d - padding * 1.5, 20, "Add", this::add);
        }

        void add() {
            AltStorage as = new AltStorage("Unknown", email.getText(), passwd.getText(), UUID.randomUUID(), AccountType.values()[accountTypeI]);
            AltContainer ac = new AltContainer(-1, -1, 0, as);
            ac.renderX = -1;
            ac.renderY = -1;
            alts.add(ac);
            Objects.requireNonNull(client).setScreen(parent);
        }

        boolean isAddApplicable() {
            if (AccountType.values()[accountTypeI] == AccountType.CRACKED && !email.getText().isEmpty()) return true;
            else return !email.getText().isEmpty() && !passwd.getText().isEmpty();
        }

        void cycle() {
            accountTypeI++;
            if (accountTypeI >= AccountType.values().length) accountTypeI = 0;
            type.text = "Type: " + AccountType.values()[accountTypeI].s;
        }

        @Override
        public void onFastTick() {
            for (ThemedButton button : buttons) {
                button.tickAnim();
            }
            type.tickAnim();
            add.tickAnim();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (ThemedButton themedButton : buttons) {
                themedButton.clicked(mouseX, mouseY);
            }
            email.mouseClicked(mouseX, mouseY, button);
            passwd.mouseClicked(mouseX, mouseY, button);
            type.clicked(mouseX, mouseY);
            add.clicked(mouseX, mouseY);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
            if (parent != null) {
                parent.renderInternal(stack, mouseX, mouseY, delta);
            }
            Renderer.R2D.renderQuad(stack, backgroundOverlay, 0, 0, width, height);

            for (ThemedButton button : buttons) {
                button.render(stack, mouseX, mouseY);
            }
            double centerX = width / 2d;
            double centerY = height / 2d;
            Renderer.R2D.renderRoundedQuad(stack, overlayBackground, centerX - widgetWid / 2d, centerY - widgetHei / 2d, centerX + widgetWid / 2d, centerY + widgetHei / 2d, 10, 10);
            stack.push();

            double originX = width / 2d - widgetWid / 2d;
            double originY = height / 2d - widgetHei / 2d;
            title.drawString(stack, "Add account", (float) (originX + padding), (float) (originY + padding), 0xFFFFFF, false);
            FontRenderers.getNormal().drawString(stack, "Add another account here", (float) (originX + padding), (float) (originY + padding + title.getMarginHeight()), 0xAAAAAA, false);
            email.setX(originX + padding);
            email.setY(originY + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding);
            email.setWidth(widgetWid - padding * 2);
            email.render(stack, mouseX, mouseY, 0);
            passwd.setX(originX + padding);
            passwd.setY(originY + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding + email.getHeight() + padding);
            passwd.setWidth(widgetWid - padding * 2);
            passwd.render(stack, mouseX, mouseY, 0);
            type.x = originX + padding;
            type.y = originY + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding;
            type.render(stack, mouseX, mouseY);
            add.x = originX + padding + type.width + padding;
            add.y = originY + padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding;
            add.setEnabled(isAddApplicable());
            add.render(stack, mouseX, mouseY);
            widgetHei = padding + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding + email.getHeight() + padding + passwd.getHeight() + padding + type.height + padding;
            stack.pop();
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            email.charTyped(chr, modifiers);
            passwd.charTyped(chr, modifiers);
            return super.charTyped(chr, modifiers);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            email.keyPressed(keyCode, scanCode, modifiers);
            passwd.keyPressed(keyCode, scanCode, modifiers);
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        enum AccountType {
            MOJANG("Mojang"), MICROSOFT("Microsoft"), CRACKED("Cracked");

            final String s;

            AccountType(String s) {
                this.s = s;
            }
        }
    }

    public class AltContainer {
        final AltStorage storage;
        Texture tex;
        boolean texLoaded = false;
        float animProgress = 0;
        boolean isHovered = false;
        double x, y, width, renderX, renderY;


        public AltContainer(double x, double y, double width, AltStorage inner) {
            this.storage = inner;
            this.tex = new Texture(DefaultSkinHelper.getTexture(inner.cachedUuid));
            this.x = x;
            this.y = y;
            this.width = width;
            UUID uuid = inner.cachedUuid;
            if (texCache.containsKey(uuid)) {
                this.tex = texCache.get(uuid);
            } else {
                downloadTexture();
            }
        }

        void downloadTexture() {
            HttpRequest hr = HttpRequest.newBuilder().uri(URI.create("https://crafatar.com/avatars/" + this.storage.cachedUuid + "?overlay")).header("User-Agent", "why").build();
            downloader.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", stream);
                    byte[] bytes = stream.toByteArray();

                    ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                    data.flip();
                    NativeImage img = NativeImage.read(data);
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(img);

                    CoffeeClientMain.client.execute(() -> {
                        this.tex = new Texture(("dynamic/tex_" + this.storage.cachedUuid.hashCode() + "_" + (Math.random() + "").split("\\.")[1]).toLowerCase());
                        CoffeeClientMain.client.getTextureManager().registerTexture(this.tex, texture);
                        texCache.put(this.storage.cachedUuid, this.tex);
                        texLoaded = true;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        public double getHeight() {
            return 50d;
        }

        public void login() {
            if (storage.didLogin) {
                return;
            }
            storage.didLogin = true;
            try {
                MinecraftAuthenticator auth = new MinecraftAuthenticator();
                MinecraftToken token = switch (storage.type) {
                    case MOJANG -> auth.login(storage.email, storage.password);
                    case MICROSOFT -> auth.loginWithMicrosoft(storage.email, storage.password);
                    case CRACKED -> null;
                };
                if (token == null && storage.password.equals("")) {
                    storage.valid = true;
                    storage.cachedUuid = UUID.randomUUID();
                    storage.cachedName = storage.email;
                    storage.accessToken = "coffee";
                    return;
                }
                if (token == null) {
                    throw new NullPointerException();
                }
                storage.accessToken = token.getAccessToken();
                MinecraftProfile profile = auth.getGameProfile(token);
                storage.cachedName = profile.getUsername();
                storage.cachedUuid = profile.getUuid();
                downloadTexture();
                storage.valid = true;
            } catch (Exception ignored) {
                storage.valid = false;
            }
        }

        public void tickAnim() {
            double d = 0.04;
            if (!isHovered) {
                d *= -1;
            }
            animProgress += d;
            animProgress = MathHelper.clamp(animProgress, 0, 1);
            if (renderX != -1) renderX = Transitions.transition(renderX, x, 7, 0.0001);
            if (renderY != -1) renderY = Transitions.transition(renderY, y, 7, 0.0001);
        }

        boolean inBounds(double cx, double cy) {
            return cx >= renderX && cx < renderX + width && cy >= renderY && cy < renderY + getHeight();
        }

        double easeInOutQuint(double x) {
            return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
        }

        public void render(MatrixStack stack, double mx, double my) {
            isHovered = inBounds(mx, my);
            stack.push();
            double originX = -width / 2d;
            double originY = -getHeight() / 2d;
            stack.translate(renderX + width / 2d, renderY + getHeight() / 2d, 0);
            float animProgress = (float) easeInOutQuint(this.animProgress);
            stack.scale(MathHelper.lerp(animProgress, 1f, 0.99f), MathHelper.lerp(animProgress, 1f, 0.99f), 1f);
            Renderer.R2D.renderRoundedQuad(stack, pillColor, originX, originY, originX + width, originY + getHeight(), 10, 10);
            double padding = 5;
            double texWidth = getHeight() - padding * 2;
            double texHeight = getHeight() - padding * 2;

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Renderer.R2D.renderRoundedQuadInternal(stack.peek()
                    .getPositionMatrix(), 0, 0, 0, 1, originX + padding, originY + padding, originX + padding + texWidth, originY + padding + texHeight, 6, 10);

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, tex);
            if (texLoaded)
                Renderer.R2D.renderTexture(stack, originX + padding, originY + padding, texWidth, texHeight, 0, 0, 64, 64, 64, 64);
            else
                Renderer.R2D.renderTexture(stack, originX + padding, originY + padding, texWidth, texHeight, 8, 8, 8, 8, 64, 64); // default skin

            String mail = this.storage.email;
            String[] mailPart = mail.split("@");
            String domain = mailPart[mailPart.length - 1];
            String mailN = String.join("@", Arrays.copyOfRange(mailPart, 0, mailPart.length - 1));
            if (censorEmail) {
                mailN = "*".repeat(mailN.length());
            }
            mail = mailN + "@" + domain;

            PropEntry[] props = new PropEntry[]{new PropEntry(this.storage.cachedName, FontRenderers.getCustomNormal(22), 0xFFFFFF),
                    new PropEntry("Email: " + mail, FontRenderers.getNormal(), 0xAAAAAA)
                    , new PropEntry("Type: " + this.storage.type.s, FontRenderers.getNormal(), 0xAAAAAA),
                    new PropEntry(storage.valid ? "" : "Invalid alt!", FontRenderers.getNormal(), 0xFF3333)};
            float propsHeight = Arrays.stream(props).map(propEntry -> propEntry.cfr.getFontHeight()).reduce(Float::sum).orElse(0f);
            float propsOffset = (float) (getHeight() / 2f - propsHeight / 2f);
            for (PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (originX + padding + texWidth + padding), (float) (originY + propsOffset), prop.color, false);
                propsOffset += prop.cfr.getFontHeight();
            }
            if (isLoggingIn.get() && selectedAlt == this) {
                double fromTop = getHeight() / 2d;
                Renderer.R2D.renderLoadingSpinner(stack, Utils.getCurrentRGB(), originX + width - fromTop, originY + fromTop, 10, 10);
            }
            stack.pop();
        }

        public void clicked(double mx, double my) {
            if (inBounds(mx, my)) {
                setSelectedAlt(this);
            }
        }

        public static record PropEntry(String name, FontAdapter cfr, int color) {

        }
    }
}