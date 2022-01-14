package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {
    private static HandledScreen<?> spoofedScreen = null;

    static {
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof CloseHandledScreenC2SPacket && spoofedScreen != null) {
                spoofedScreen.removed();
                spoofedScreen = null;
            }
        });
    }

    ButtonWidget openSpoof = null;


    public HandledScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "init", at = @At("RETURN"))
    void real(CallbackInfo ci) {
        ButtonWidget closeSpoof = new ButtonWidget(5, 5, 100, 20, Text.of("Close spoof"), button -> {
            spoofedScreen = (HandledScreen<?>) (Object) this;
            // go off menu client side but save it in spoofedScreen
            CoffeeClientMain.client.setScreen(null);
            Objects.requireNonNull(CoffeeClientMain.client.player).currentScreenHandler = CoffeeClientMain.client.player.playerScreenHandler;
        });
        addDrawableChild(closeSpoof);
        openSpoof = new ButtonWidget(5, 30, 100, 20, Text.of("Open spoofed"), button -> {
            if (spoofedScreen == null) {
                return;
            }
            Objects.requireNonNull(CoffeeClientMain.client.player).currentScreenHandler = spoofedScreen.getScreenHandler();
            CoffeeClientMain.client.setScreen(spoofedScreen);
        });
        addDrawableChild(openSpoof);
    }

    @Inject(method = "render", at = @At("HEAD"))
    void real1(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (openSpoof != null) {
            openSpoof.active = spoofedScreen != null;
        }
    }

    @Inject(method = "removed", at = @At("HEAD"), cancellable = true)
    void real2(CallbackInfo ci) {
        if (this.equals(spoofedScreen)) {
            ci.cancel(); // dont notify the normal handler if we spoof this screen
        }
    }

}
