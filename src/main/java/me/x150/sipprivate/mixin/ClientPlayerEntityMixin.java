package me.x150.sipprivate.mixin;

import me.x150.sipprivate.util.ConfigManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD")) void tick(CallbackInfo ci) {
        if (!ConfigManager.enabled) {
            ConfigManager.enableModules();
        }
    }
}
