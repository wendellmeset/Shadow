/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(Screen.class)
public class ScreenMixin {
    private static final Color c = new Color(10, 10, 10);
    @Shadow
    public int height;

    @Shadow
    public int width;

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    void real(int vOffset, CallbackInfo ci) {
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        ci.cancel();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).color(r, g, b, 1f).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).color(r, g, b, 1f).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).color(r, g, b, 1f).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).color(r, g, b, 1f).next();
        tessellator.draw();
    }
}
