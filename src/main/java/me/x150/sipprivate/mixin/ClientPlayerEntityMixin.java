package me.x150.sipprivate.mixin;

import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.misc.PortalGUI;
import me.x150.sipprivate.helper.manager.ConfigManager;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void atomic_preTick(CallbackInfo ci) {
        Utils.TickManager.tick();
        if (!ConfigManager.enabled) {
            ConfigManager.enableModules();
        }
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) {
                module.tick();
            }
        }
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;isPauseScreen()Z"))
    public boolean atomic_overwriteIsPauseScreen(Screen screen) {
        return Objects.requireNonNull(ModuleRegistry.getByClass(PortalGUI.class)).isEnabled() || screen.isPauseScreen();
    }
}
