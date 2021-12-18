package me.x150.sipprivate.mixin;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.util.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class) public class MinecraftClientMixin {
    @Inject(method = "stop", at = @At("HEAD")) void real(CallbackInfo ci) {
        ConfigManager.saveState();
    }

    @Inject(method = "<init>", at = @At("TAIL")) void atomic_postInit(RunArgs args, CallbackInfo ci) {
        SipoverPrivate.INSTANCE.postWindowInit();
    }

    @Inject(method = "setScreen", at = @At("HEAD")) void atomic_preSetScreen(Screen screen, CallbackInfo ci) {
        SipoverPrivate.lastScreenChange = System.currentTimeMillis();
    }
}
