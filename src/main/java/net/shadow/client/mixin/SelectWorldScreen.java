/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screen.world.SelectWorldScreen.class)
public class SelectWorldScreen extends Screen {
    public SelectWorldScreen() {
        super(Text.of(""));
    }

    @Inject(method = "render", at = @At("HEAD"))
    void a(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderBackground(matrices);
    }
}
