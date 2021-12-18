package me.x150.sipprivate;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SipoverPrivate implements ModInitializer {

    public static final String MOD_ID   = "sipoverprivate";
    public static final String MOD_NAME = "SipoverPrivate";
    public static Logger LOGGER = LogManager.getLogger();
    public static MinecraftClient client = MinecraftClient.getInstance();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    @Override public void onInitialize() {
        log(Level.INFO, "Initializing");
        //TODO: Initializer
    }

}