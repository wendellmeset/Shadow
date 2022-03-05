package me.x150.coffee.mixin;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.screen.NbtEditorScreen;
import me.x150.coffee.feature.gui.widget.RoundButton;
import me.x150.coffee.helper.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin extends Screen {

    protected CreativeInventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    void postInit(CallbackInfo ci) {
        RoundButton nbtEdit = new RoundButton(RoundButton.STANDARD, 5, 5, 64, 20, "NBT editor", () -> {
            if (CoffeeClientMain.client.player.getInventory().getMainHandStack().isEmpty()) {
                Utils.Logging.error("You need to hold an item!");
            } else {
                CoffeeClientMain.client.setScreen(new NbtEditorScreen(CoffeeClientMain.client.player.getInventory().getMainHandStack()));
            }
        });
        addDrawableChild(nbtEdit);
    }
}
