package me.x150.coffee.feature.gui.hud.element;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.module.ModuleRegistry;
import me.x150.coffee.feature.module.impl.render.TargetHud;
import me.x150.coffee.helper.render.MSAAFramebuffer;
import net.minecraft.client.util.math.MatrixStack;

public class TargetHUD extends HudElement {

    public TargetHUD() {
        super("Target HUD", CoffeeClientMain.client.getWindow().getScaledWidth() / 2f + 10, CoffeeClientMain.client.getWindow()
                .getScaledHeight() / 2f + 10, TargetHud.modalWidth, TargetHud.modalHeight);
    }

    @Override
    public void renderIntern(MatrixStack stack) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> ModuleRegistry.getByClass(TargetHud.class).draw(stack));
    }
}
