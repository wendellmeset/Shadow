package me.x150.sipprivate.mixin;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.screen.AltManagerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class) public class TitleScreenMixin extends Screen {
    public TitleScreenMixin() {
        super(Text.of(""));
    }

    @Inject(method = "init", at = @At("RETURN")) void real(CallbackInfo ci) {
        ButtonWidget bw = new ButtonWidget(5, 5, 100, 20, Text.of("Alts"), button -> {
            CoffeeClientMain.client.setScreen(AltManagerScreen.instance());
        });
        addDrawableChild(bw);
    }
}
