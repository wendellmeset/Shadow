package me.x150.sipprivate;

import me.x150.sipprivate.keybinding.KeybindingManager;
import me.x150.sipprivate.util.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class SipoverPrivate implements ModInitializer {

    public static final String          MOD_ID   = "sipoverprivate";
    public static final String          MOD_NAME = "SipoverPrivate";
    public static       Logger          LOGGER   = LogManager.getLogger();
    public static       MinecraftClient client   = MinecraftClient.getInstance();

    public static File BASE = new File(MinecraftClient.getInstance().runDirectory, "sip");

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    @Override public void onInitialize() {
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveState));
        if (BASE.exists() && !BASE.isDirectory()) {
            BASE.delete();
        }
        if (!BASE.exists()) {
            BASE.mkdir();
        }
        KeybindingManager.init();
        ConfigManager.loadState();
        log(Level.INFO, "Done initializing");
        //TODO: Initializer
    }

}