package me.x150.sipprivate.mixin;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.util.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
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
}
