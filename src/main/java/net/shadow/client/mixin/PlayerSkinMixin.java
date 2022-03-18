/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

@Debug(export = true)
@Mixin(PlayerSkinProvider.class)
public class PlayerSkinMixin {
    private static JsonObject capes;
    @Shadow
    @Final
    private MinecraftSessionService sessionService;

    //I cant even launch mc with this! it doesnt work
    // skill issue, i would say
    @Inject(method = "method_4653", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V",
            shift = At.Shift.BEFORE
    ), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    void real(GameProfile gameProfile, boolean bl, PlayerSkinProvider.SkinTextureAvailableCallback skinTextureAvailableCallback, CallbackInfo ci, Map<Type, MinecraftProfileTexture> map) {
        addCape(gameProfile, map);
    }

    private void addCape(GameProfile profile,
                         Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
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
