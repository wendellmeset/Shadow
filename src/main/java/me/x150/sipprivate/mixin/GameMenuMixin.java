package me.x150.sipprivate.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuMixin extends Screen {

    protected GameMenuMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    void initWidgets(CallbackInfo ci) {
//        ButtonWidget bw = new ButtonWidget(5, 5, 100, 20, Text.of("Coffee console"), button -> {
//            CoffeeClientMain.client.setScreen(CoffeeConsoleScreen.instance());
//        });
//
//        addDrawableChild(bw);
    }
}
