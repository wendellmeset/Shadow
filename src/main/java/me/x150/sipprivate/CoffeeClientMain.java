package me.x150.sipprivate;

import me.x150.sipprivate.feature.command.CommandRegistry;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.feature.gui.notifications.NotificationRenderer;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.helper.Rotations;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PostInitEvent;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.font.adapter.impl.VanillaFontRenderer;
import me.x150.sipprivate.helper.font.render.GlyphPageFontRenderer;
import me.x150.sipprivate.helper.manager.ConfigManager;
import me.x150.sipprivate.helper.util.Utils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CoffeeClientMain implements ModInitializer {

    public static final String MOD_ID = "sipoverprivate";
    public static final String MOD_NAME = "SipoverPrivate";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final File BASE = new File(MinecraftClient.getInstance().runDirectory, "sip");
    public static final List<ResourceEntry> resources = List.of(
            new ResourceEntry(new Identifier("coffeeclient", "background.jpg"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/background.jpg"),
            new ResourceEntry(new Identifier("coffeeclient", "notification/error.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/error.png"),
            new ResourceEntry(new Identifier("coffeeclient", "notification/info.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/info.png"),
            new ResourceEntry(new Identifier("coffeeclient", "notification/success.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/success.png"),
            new ResourceEntry(new Identifier("coffeeclient", "notification/warning.png"), "https://gitlab.com/0x151/coffee-fs/-/raw/main/warning.png")
    );
    public static long lastScreenChange = System.currentTimeMillis();
    public static CoffeeClientMain INSTANCE;
    public static Thread MODULE_FTTICKER;
    public static Thread FAST_TICKER;
    public boolean initialized = false;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public void downloadAndRegisterTextures() {
        HttpClient downloader = HttpClient.newHttpClient();
        for (ResourceEntry resource : resources) {
            log(Level.INFO, "Downloading " + resource.url);
            try {
                HttpRequest hrq = HttpRequest.newBuilder()
                        .uri(URI.create(resource.url))
                        .build();
                HttpResponse<byte[]> b = downloader.send(hrq, HttpResponse.BodyHandlers.ofByteArray());
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(b.body()));
                Utils.registerBufferedImageTexture(resource.tex, bi);
                log(Level.INFO, "Downloaded " + resource.url);
            } catch (Exception ignored) {
                log(Level.ERROR, "Failed to download " + resource.url);
            }

        }
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveState));
        if (BASE.exists() && !BASE.isDirectory()) {
            BASE.delete();
        }
        if (!BASE.exists()) {
            BASE.mkdir();
        }
        //        KeybindingManager.init();
        ConfigManager.loadState();
        log(Level.INFO, "Done initializing");
        //TODO: Initializer
    }

    void initFonts() {
        FontRenderers.setNormal(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 17, false, false, false)));
        FontRenderers.setTitle(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 25, false, false, false)));
        FontRenderers.setMono(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 17, false, false, false)));
        FontRenderers.setVanilla(new VanillaFontRenderer());
    }

    void tickModulesNWC() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick_NWC();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickModules() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickGuiSystem() {
        NotificationRenderer.onFastTick();
        try {
            if (client.currentScreen != null) {
                if (client.currentScreen instanceof FastTickable tickable) {
                    tickable.onFastTick();
                }
                for (Element child : new ArrayList<>(client.currentScreen.children())) { // wow, I hate this
                    if (child instanceof FastTickable t) {
                        t.onFastTick();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void postWindowInit() {
        initialized = true;
        initFonts();
        ConfigManager.loadState();
        MODULE_FTTICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickModulesNWC(); // always ticks even when we're not in a world
                if (client.player == null || client.world == null) {
                    continue;
                }
                tickModules(); // only ticks when we're in a world
            }
        }, "100_tps_ticker:modules");
        FAST_TICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickGuiSystem(); // ticks gui elements
                //                Themes.tickThemes(); // Tick themes
                if (client.player == null || client.world == null) {
                    continue;
                }
                Rotations.update(); // updates rotations, again only if we are in a world
            }
        }, "100_tps_ticker:gui");
        MODULE_FTTICKER.start();
        FAST_TICKER.start();
        //        ModuleRegistry.sortModulesPostInit();
        CommandRegistry.init();
        System.out.println("sending post init");
        Events.fireEvent(EventType.POST_INIT, new PostInitEvent());
    }

    public record ResourceEntry(Identifier tex, String url) {
    }

}