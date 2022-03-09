package net.shadow.client.feature.gui.hud.element;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.CoffeeClientMain;
import net.shadow.client.feature.module.ModuleRegistry;
import net.shadow.client.feature.module.impl.render.TargetHud;
import net.shadow.client.helper.render.MSAAFramebuffer;

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
