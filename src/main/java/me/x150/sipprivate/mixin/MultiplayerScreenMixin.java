package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.screen.ProxyManagerScreen;
import me.x150.sipprivate.feature.gui.widget.RoundButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    public MultiplayerScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "init", at = @At("RETURN"))
    void init(CallbackInfo ci) {
        double sourceY = 32 / 2d - 20 / 2d;
        RoundButton proxies = new RoundButton(new Color(40, 40, 40), 5, sourceY, 60, 20, "Proxies", () -> {
            CoffeeClientMain.client.setScreen(new ProxyManagerScreen(this));
        });
        addDrawableChild(proxies);
    }
}
