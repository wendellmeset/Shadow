package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.CommandRegistry;
import me.x150.sipprivate.feature.gui.screen.CoffeeConsoleScreen;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.misc.InfChatLength;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.util.Utils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class AChatScreenMixin extends Screen {

    @Shadow
    protected TextFieldWidget chatField;

    protected AChatScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    void atomic_interceptChatMessage(ChatScreen instance, String s) {
        if (s.startsWith(".")) { // filter all messages starting with .
            if (s.equalsIgnoreCase(".console")) {
                Utils.TickManager.runInNTicks(2, () -> CoffeeClientMain.client.setScreen(CoffeeConsoleScreen.instance()));
            } else CommandRegistry.execute(s.substring(1)); // cut off prefix
        } else {
            instance.sendMessage(s); // else, go
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String t = chatField.getText();
        if (t.startsWith(".")) {
            String note = "If you need a bigger console, do \".console\"";
            double len = FontRenderers.getNormal().getStringWidth(note) + 1;
            FontRenderers.getNormal().drawString(matrices, note, width - len - 2, height - 15 - FontRenderers.getNormal().getMarginHeight(), 0xFFFFFF);
        }
    }

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD"))
    public void atomic_preChatFieldUpdate(String chatText, CallbackInfo ci) {
        chatField.setMaxLength((ModuleRegistry.getByClass(InfChatLength.class).isEnabled()) ? Integer.MAX_VALUE : 256);
    }
}
