package me.x150.sipprivate.feature.gui.hud.element;

import me.x150.sipprivate.SipoverPrivate;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.render.TargetHud;
import net.minecraft.client.util.math.MatrixStack;

public class TargetHUD extends HudElement {

    public TargetHUD() {
        super("Target HUD", SipoverPrivate.client.getWindow().getScaledWidth() / 2f + 10, SipoverPrivate.client.getWindow().getScaledHeight() / 2f + 10, TargetHud.modalWidth, TargetHud.modalHeight);
    }

    @Override public void renderIntern(MatrixStack stack) {
        ModuleRegistry.getByClass(TargetHud.class).draw(stack);
    }
}
