/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;
import net.shadow.client.feature.command.CommandRegistry;
import net.shadow.client.feature.gui.screen.ConsoleScreen;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.misc.ClientSettings;
import net.shadow.client.feature.module.impl.misc.InfChatLength;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.MSAAFramebuffer;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ChatScreen.class)
public class AChatScreenMixin extends Screen {

    @Shadow
    protected TextFieldWidget chatField;

    protected AChatScreenMixin(Text title) {
        super(title);
    }

    private String getPrefix() {
        return ModuleRegistry.getByClass(ClientSettings.class).getSs().getValue();
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;)V"))
    void atomic_interceptChatMessage(ChatScreen instance, String s) {
        String p = getPrefix();
        if (s.startsWith(p)) { // filter all messages starting with .
            ShadowMain.client.inGameHud.getChatHud().addToMessageHistory(s);
            if (s.equalsIgnoreCase(p + "console")) {
                Utils.TickManager.runInNTicks(2, () -> ShadowMain.client.setScreen(ConsoleScreen.instance()));
            } else {
                CommandRegistry.execute(s.substring(p.length())); // cut off prefix
            }
        } else {
            instance.sendMessage(s); // else, go
        }
    }

    List<String> getSuggestions(String command) {
        List<String> a = new ArrayList<>();
        String[] args = command.split(" +");
        String cmd = args[0].toLowerCase();
        args = Arrays.copyOfRange(args, 1, args.length);
        if (command.endsWith(" ")) { // append empty arg when we end with a space
            String[] args1 = new String[args.length + 1];
            System.arraycopy(args, 0, args1, 0, args.length);
            args1[args1.length - 1] = "";
            args = args1;
        }
        if (args.length > 0) {
            Command c = CommandRegistry.getByAlias(cmd);
            if (c != null) {
                a = List.of(c.getSuggestions(command, args));
            } else {
                return new ArrayList<>(); // we have no command to ask -> we have no suggestions
            }
        } else {
            for (Command command1 : CommandRegistry.getCommands()) {
                for (String alias : command1.getAliases()) {
                    if (alias.toLowerCase().startsWith(cmd.toLowerCase())) {
                        a.add(alias);
                    }
                }
            }
        }
        String[] finalArgs = args;
        return finalArgs.length > 0 ? a.stream().filter(s -> s.toLowerCase().startsWith(finalArgs[finalArgs.length - 1].toLowerCase())).collect(Collectors.toList()) : a;
    }

    double padding() {
        return 5;
    }

    void renderSuggestions(MatrixStack stack) {
        String p = getPrefix();
        String cmd = chatField.getText().substring(p.length());
        if (cmd.isEmpty()) {
            return;
        }
        float cmdTWidth = ShadowMain.client.textRenderer.getWidth(cmd);
        double cmdXS = chatField.x + 5 + cmdTWidth;

        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        double probableHeight = suggestions.size() * FontRenderers.getRenderer().getMarginHeight() + padding();
        float yC = (float) (chatField.y - padding() - probableHeight);
        double probableWidth = 0;
        for (String suggestion : suggestions) {
            probableWidth = Math.max(probableWidth, FontRenderers.getRenderer().getStringWidth(suggestion) + 1);
        }
        float xC = (float) (cmdXS);
        Renderer.R2D.renderRoundedQuad(stack, new Color(30, 30, 30, 255), xC - padding(), yC - padding(), xC + probableWidth + padding(), yC + probableHeight, 5, 20);
        for (String suggestion : suggestions) {
            FontRenderers.getRenderer().drawString(stack, suggestion, xC, yC, 0xFFFFFF, false);
            yC += FontRenderers.getRenderer().getMarginHeight();
        }
    }

    void autocomplete() {
        String p = getPrefix();
        String cmd = chatField.getText().substring(p.length());
        if (cmd.isEmpty()) {
            return;
        }
        List<String> suggestions = getSuggestions(cmd);
        if (suggestions.isEmpty()) {
            return;
        }
        String[] cmdSplit = cmd.split(" +");
        if (cmd.endsWith(" ")) {
            String[] cmdSplitNew = new String[cmdSplit.length + 1];
            System.arraycopy(cmdSplit, 0, cmdSplitNew, 0, cmdSplit.length);
            cmdSplitNew[cmdSplitNew.length - 1] = "";
            cmdSplit = cmdSplitNew;
        }
        cmdSplit[cmdSplit.length - 1] = suggestions.get(0);
        chatField.setText(p + String.join(" ", cmdSplit) + " ");
        chatField.setCursorToEnd();
    }

    @Inject(method = "render", at = @At("RETURN"))
    void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String p = getPrefix();
        String t = chatField.getText();
        if (t.startsWith(p)) {
            String note = "If you need a bigger console, do \"" + p + "console\"";
            double len = FontRenderers.getRenderer().getStringWidth(note) + 1;
            FontRenderers.getRenderer().drawString(matrices, note, width - len - 2, height - 15 - FontRenderers.getRenderer().getMarginHeight(), 0xFFFFFF);
            MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
                renderSuggestions(matrices);
            });
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    void pressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        String p = getPrefix();
        if (keyCode == GLFW.GLFW_KEY_TAB && chatField.getText().startsWith(p)) {
            autocomplete();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"init()V"}, at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        chatField.setMaxLength((ModuleRegistry.getByClass(InfChatLength.class).isEnabled()) ? Integer.MAX_VALUE : 256);
    }
}
