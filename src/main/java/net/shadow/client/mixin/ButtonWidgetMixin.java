/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ClickableWidget.class)
public abstract class ButtonWidgetMixin {
    final Color unselectedColor = new Color(150, 150, 150, 75);
    final Color selectedColor = new Color(100, 100, 100, 100);
    @org.spongepowered.asm.mixin.Shadow
    public int x;
    @org.spongepowered.asm.mixin.Shadow
    public int y;
    @org.spongepowered.asm.mixin.Shadow
    protected int width;
    @org.spongepowered.asm.mixin.Shadow
    protected int height;
    double animProgress = 0;

    @org.spongepowered.asm.mixin.Shadow
    public abstract boolean isHovered();

    @org.spongepowered.asm.mixin.Shadow
    public abstract Text getMessage();

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //if (!UnloadModule.loaded) return;
//        Renderer.R2D.renderRoundedQuad(matrices, this.isHovered() ? selectedColor : unselectedColor, x, y, x + width, y + height, 5, 7);
//        FontRenderers.getRenderer().drawCenteredString(matrices, this.getMessage().getString(), this.x + this.width / 2f, this.y + (this.height - 9) / 2f, 0xFFFFFF);
//        ci.cancel();
    }


    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }
}