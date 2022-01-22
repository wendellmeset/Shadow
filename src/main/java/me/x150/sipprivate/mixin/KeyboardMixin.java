package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.KeyboardEvent;
import me.x150.sipprivate.helper.manager.KeybindingManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow
    private boolean repeatEvents;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At("RETURN"))
    void atomic_postKeyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window == this.client.getWindow()
                .getHandle() && CoffeeClientMain.client.currentScreen == null && System.currentTimeMillis() - CoffeeClientMain.lastScreenChange > 10) { // make sure we are in game and the screen has been there for at least 10 ms
            if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.world == null) {
                return; // again, make sure we are in game and exist
            }
            KeybindingManager.updateSingle(key, action);
            Events.fireEvent(EventType.KEYBOARD, new KeyboardEvent(key, action));
        }
    }

    @Inject(method = "setRepeatEvents", at = @At("HEAD"), cancellable = true)
    void repeatEvents(boolean repeatEvents, CallbackInfo ci) {
        this.repeatEvents = true;
        ci.cancel();
    }
}
