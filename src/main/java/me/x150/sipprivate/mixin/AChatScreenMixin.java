package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.CommandRegistry;
import me.x150.sipprivate.feature.gui.screen.AtomicConsoleScreen;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class) public class AChatScreenMixin {
    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    void atomic_interceptChatMessage(ChatScreen instance, String s) {
        if (s.startsWith(".")) { // filter all messages starting with .
            Utils.TickManager.runInNTicks(1, () -> {
                CoffeeClientMain.client.setScreen(AtomicConsoleScreen.instance());
                CommandRegistry.execute(s.substring(1));
            });
        } else {
            instance.sendMessage(s); // else, go
        }
    }
}
