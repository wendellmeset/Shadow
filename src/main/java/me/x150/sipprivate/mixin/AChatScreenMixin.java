package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.CommandRegistry;
import me.x150.sipprivate.feature.gui.screen.CoffeeConsoleScreen;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.misc.InfChatLength;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class AChatScreenMixin {
    @Shadow
    protected TextFieldWidget chatField;

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    void atomic_interceptChatMessage(ChatScreen instance, String s) {
        if (s.startsWith(".")) { // filter all messages starting with .
            Utils.TickManager.runInNTicks(1, () -> {
                CoffeeClientMain.client.setScreen(CoffeeConsoleScreen.instance());
                CommandRegistry.execute(s.substring(1));
            });
        } else {
            instance.sendMessage(s); // else, go
        }
    }

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void atomic_preChatFieldUpdate(String chatText, CallbackInfo ci) {
        chatField.setMaxLength((ModuleRegistry.getByClass(InfChatLength.class).isEnabled()) ? Integer.MAX_VALUE : 256);
    }
}
