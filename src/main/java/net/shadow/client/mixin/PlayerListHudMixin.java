/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.GameTexture;
import net.shadow.client.helper.IRCWebSocket;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    void a(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
        if (IRCWebSocket.knownIRCPlayers.stream().anyMatch(playerEntry -> playerEntry.uuid().equals(entry.getProfile().getId()))) {
            ci.cancel();
            double supposedWidth = 10;
            double origWidth = 782;
            double origHeight = 1000;
            double newHeight = 8;
            double newWidth = origWidth * (newHeight / origHeight);
            Utils.TickManager.runOnNextRender(() -> {
                RenderSystem.setShaderTexture(0, GameTexture.TEXTURE_ICON.getWhere());
                Renderer.R2D.renderTexture(matrices, x + width - supposedWidth / 2d - newWidth / 2d - 1, y, newWidth, newHeight, 0, 0, newWidth, newHeight, newWidth, newHeight);
            });
        }
    }
}
