package me.x150.sipprivate.mixin;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.MouseEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class) public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD")) public void atomic_dispatchMouseEvent(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == SipoverPrivate.client.getWindow().getHandle()) {
            Events.fireEvent(EventType.MOUSE_EVENT, new MouseEvent(button, action));
        }
    }
}
