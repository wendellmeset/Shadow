package net.shadow.client.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinMixin {
    private static JsonObject capes;
    @Shadow
    @Final
    private MinecraftSessionService sessionService;

    @Inject(at = {@At("HEAD")},
            method = {
                    "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V"},
            cancellable = true)
    private void onLoadSkin(GameProfile profile,
                            PlayerSkinProvider.SkinTextureAvailableCallback callback,
                            boolean requireSecure, CallbackInfo ci) {

        Runnable runnable = () -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map =
                    Maps.newHashMap();

            try {
                map.putAll(sessionService.getTextures(profile, requireSecure));
            } catch (InsecureTextureException ignored) {

            }

            if (map.isEmpty()) {
                profile.getProperties().clear();
                if (profile.getId().equals(MinecraftClient.getInstance()
                        .getSession().getProfile().getId())) {
                    profile.getProperties().putAll(
                            MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(sessionService.getTextures(profile, false));
                } else {
                    sessionService.fillProfileProperties(profile,
                            requireSecure);

                    try {
                        map.putAll(
                                sessionService.getTextures(profile, requireSecure));
                    } catch (InsecureTextureException ignored) {

                    }
                }
            }

            addCape(profile, map);

            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(Type.SKIN, Type.CAPE).forEach((type) -> {
                if (map.containsKey(type))
                    loadSkin(map.get(type), type, callback);
            })));
        };
        Util.getMainWorkerExecutor().execute(runnable);

        ci.cancel();
    }

    private void addCape(GameProfile profile,
                         HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
        String name = profile.getName();
        String uuid = profile.getId().toString();

        try {
            if (capes == null)
                setupCapes();

            if (capes.has(name)) {
                String capeURL = capes.get(name).getAsString();
                map.put(Type.CAPE, new MinecraftProfileTexture(capeURL, null));

            } else if (capes.has(uuid)) {
                String capeURL = capes.get(uuid).getAsString();
                map.put(Type.CAPE, new MinecraftProfileTexture(capeURL, null));
            }

        } catch (Exception e) {
            System.err.println("[Shadow] Failed to load cape for '" + name
                    + "' (" + uuid + ")");

            e.printStackTrace();
        }
    }

    private void setupCapes() {
        try {
            URL url = new URL("https://moleapi.pythonanywhere.com/retrieve");
            capes = new JsonParser().parse(new InputStreamReader(url.openStream())).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("[Shadow] Failed to load capes from API");
            e.printStackTrace();
        }
    }

    @Shadow
    private Identifier loadSkin(MinecraftProfileTexture profileTexture,
                                Type type,
                                @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback) {
        return null;
    }
}
