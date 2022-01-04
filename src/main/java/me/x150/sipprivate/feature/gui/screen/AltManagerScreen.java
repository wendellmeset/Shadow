package me.x150.sipprivate.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40C;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AltManagerScreen extends AntiAliasedScreen implements FastTickable {
    static HttpClient downloader = HttpClient.newHttpClient();
    private static AltManagerScreen instance = null;
    List<AltContainer> alts = new ArrayList<>();
    ThemedButton       add, exit, remove, edit, login;
    double leftWidth = 200;
    AltContainer selectedAlt;
    ClientFontRenderer title      = FontRenderers.getCustomNormal(40);
    ClientFontRenderer titleSmall = FontRenderers.getCustomNormal(30);

    private AltManagerScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
        //        super(-1);
    }

    public static AltManagerScreen instance() {
        if (instance == null) {
            instance = new AltManagerScreen();
        }
        return instance;
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

    @Override protected void init() {
        alts.clear();
        add = new ThemedButton(width - 60 - 5 - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 60, 20, "Add", () -> {
            System.out.println("REAL");
        });
        exit = new ThemedButton(width - 20 - getPadding(), 10 + title.getMarginHeight() / 2d - 20 / 2d, 20, 20, "X", () -> {
            client.setScreen(null);
        });
        double offset = 0;

        for (int i = 0; i < 3; i++) {
            AltContainer ad = new AltContainer("Amog", "abc@gmail.com", "abc", UUID.fromString("2f2b69af-7651-4960-8379-6df999b88d75"), getPadding(), getHeaderHeight() + offset, width - (getPadding() + leftWidth + getPadding() * 2));
            offset += ad.getHeight() + getPadding();
            alts.add(ad);
        }

    }

    @Override public void onFastTick() {
        for (AltContainer alt : alts) {
            alt.tickAnim();
        }
        add.tickAnim();
        exit.tickAnim();
    }

    @Override protected void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Renderer.R2D.fill(stack, new Color(220, 220, 220), 0, 0, width, height);
        title.drawString(stack, "Coffee", 10, 10, 0, false);
        titleSmall.drawString(stack, "Alt manager", 10 + title.getStringWidth("Coffee") + 5, 10 + title.getMarginHeight() - titleSmall.getMarginHeight() - 1, 0, false);
        add.render(stack, mouseX, mouseY);
        exit.render(stack, mouseX, mouseY);
        for (AltContainer alt : alts) {
            alt.render(stack, mouseX, mouseY);
        }

        double fromX = width - (leftWidth + getPadding());
        double toX = width - getPadding();
        double fromY = getHeaderHeight();
        double toY = height - getPadding();

        Renderer.R2D.renderRoundedQuad(stack, new Color(255, 255, 255, 100), fromX, fromY, toX, toY, 10, 10);
        if (selectedAlt != null) {
            double texWidth = 64;
            double texHeight = 64;
            double padding = 5;
            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Renderer.R2D.renderRoundedQuadInternal(stack.peek().getPositionMatrix(), 0, 0, 0, 1, fromX + padding, fromY + padding, fromX + padding + texWidth, fromY + padding + texHeight, 6, 10);

            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            RenderSystem.setShaderTexture(0, selectedAlt.tex);
            Renderer.R2D.drawTexture(stack, fromX + padding, fromY + padding, texWidth, texHeight, 0, 0, 64, 64, 64, 64);
            RenderSystem.defaultBlendFunc();
            AltContainer.PropEntry[] props = new AltContainer.PropEntry[]{new AltContainer.PropEntry(selectedAlt.name, FontRenderers.getCustomNormal(22), 0),
                    new AltContainer.PropEntry(selectedAlt.email, FontRenderers.getNormal(), 0x555555),
                    new AltContainer.PropEntry("Password: " + selectedAlt.password, FontRenderers.getNormal(), 0x555555)};
            float propsOffset = (float) (fromY + padding);
            for (AltContainer.PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (fromX + padding + texWidth + padding), propsOffset, prop.color, false);
                propsOffset += prop.cfr.getMarginHeight();
            }
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AltContainer alt : alts) {
            alt.clicked(mouseX, mouseY);
        }
        add.clicked(mouseX, mouseY);
        exit.clicked(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    static class ThemedButton {
        String   text;
        Runnable onPress;
        double   x, y, width, height;
        double  animProgress = 0;
        boolean isHovered    = false;

        public ThemedButton(double x, double y, double w, double h, String t, Runnable a) {
            this.onPress = a;
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.text = t;
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
            isHovered = inBounds(mx, my);
            matrices.push();
            matrices.translate(x + width / 2d, y + height / 2d, 0);
            float animProgress = (float) easeInOutQuint(this.animProgress);
            matrices.scale(MathHelper.lerp(animProgress, 1f, 0.95f), MathHelper.lerp(animProgress, 1f, 0.95f), 1f);
            double originX = -width / 2d;
            double originY = -height / 2d;
            Renderer.R2D.renderRoundedQuad(matrices, new Color(255, 255, 255, 100), originX, originY, width / 2d, height / 2d, 10, 10);
            FontRenderers.getNormal().drawString(matrices, text, -(FontRenderers.getNormal().getStringWidth(text) + 2) / 2f, -FontRenderers.getNormal().getMarginHeight() / 2f, 0, false);
            matrices.pop();
        }

        public void clicked(double mx, double my) {
            if (inBounds(mx, my)) {
                onPress.run();
            }
        }
    }

    class AltContainer {
        static Map<UUID, Identifier> texCache = new HashMap<>();

        String name, email, password;
        Identifier tex;
        float      animProgress = 0;
        boolean    isHovered    = false;
        double     x, y, width;
        UUID uuid;

        public AltContainer(String name, String email, String password, UUID uuid, double x, double y, double width) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.tex = DefaultSkinHelper.getTexture(uuid);
            this.x = x;
            this.y = y;
            this.width = width;
            this.uuid = uuid;
            if (texCache.containsKey(uuid)) {
                this.tex = texCache.get(uuid);
            } else {
                HttpRequest hr = HttpRequest.newBuilder().uri(URI.create("https://crafatar.com/avatars/" + uuid + "?overlay")).header("User-Agent", "why").build();
                downloader.sendAsync(hr, HttpResponse.BodyHandlers.ofByteArray()).thenAccept(httpResponse -> {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(ImageIO.read(new ByteArrayInputStream(httpResponse.body())), "png", baos);
                        byte[] bytes = baos.toByteArray();

                        ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
                        data.flip();
                        NativeImage ni = NativeImage.read(data);
                        System.out.println(ni);
                        NativeImageBackedTexture tex = new NativeImageBackedTexture(ni);

                        CoffeeClientMain.client.execute(() -> {
                            this.tex = new Identifier("atomic", "tex_" + uuid.hashCode() + "_" + (Math.random() + "").split("\\.")[1]);
                            CoffeeClientMain.client.getTextureManager().registerTexture(this.tex, tex);
                            texCache.put(uuid, this.tex);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        public double getHeight() {
            return 50d;
        }

        public void tickAnim() {
            double d = 0.04;
            if (!isHovered) {
                d *= -1;
            }
            animProgress += d;
            animProgress = MathHelper.clamp(animProgress, 0, 1);
        }

        boolean inBounds(double cx, double cy) {
            return cx >= x && cx < x + width && cy >= y && cy < y + getHeight();
        }

        double easeInOutQuint(double x) {
            return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
        }

        public void render(MatrixStack stack, double mx, double my) {
            isHovered = inBounds(mx, my);
            stack.push();
            double originX = -width / 2d;
            double originY = -getHeight() / 2d;
            stack.translate(x + width / 2d, y + getHeight() / 2d, 0);
            float animProgress = (float) easeInOutQuint(this.animProgress);
            stack.scale(MathHelper.lerp(animProgress, 1f, 0.99f), MathHelper.lerp(animProgress, 1f, 0.99f), 1f);
            Renderer.R2D.renderRoundedQuad(stack, new Color(255, 255, 255, 100), originX, originY, originX + width, originY + getHeight(), 10, 10);
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
            Renderer.R2D.drawTexture(stack, originX + padding, originY + padding, texWidth, texHeight, 0, 0, 64, 64, 64, 64);

            PropEntry[] props = new PropEntry[]{new PropEntry(this.name, FontRenderers.getCustomNormal(22), 0), new PropEntry("Email: " + this.email, FontRenderers.getNormal(), 0x555555),
                    new PropEntry("Password: " + this.password, FontRenderers.getNormal(), 0x555555)};
            float propsHeight = Arrays.stream(props).map(propEntry -> propEntry.cfr.getFontHeight()).reduce(Float::sum).orElse(0f);
            float propsOffset = (float) (getHeight() / 2f - propsHeight / 2f);
            for (PropEntry prop : props) {
                prop.cfr.drawString(stack, prop.name, (float) (originX + padding + texWidth + padding), (float) (originY + propsOffset), prop.color, false);
                propsOffset += prop.cfr.getFontHeight();
            }
            stack.pop();
        }

        public void clicked(double mx, double my) {
            if (inBounds(mx, my)) {
                //                System.out.println("Clicked");
                setSelectedAlt(this);
            }
        }

        record PropEntry(String name, FontAdapter cfr, int color) {

        }
    }
}
