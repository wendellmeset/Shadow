package me.x150.sipprivate.feature.gui.hud.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.helper.Texture;
import me.x150.sipprivate.helper.font.FontRenderers;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class Taco extends HudElement {
    public Taco() {
        super("Taco", 0, CoffeeClientMain.client.getWindow().getScaledHeight(), 100, 100);
    }

    @Override
    public void renderIntern(MatrixStack stack) {
        if (!me.x150.sipprivate.feature.command.impl.Taco.config.enabled) {
            return;
        }
        me.x150.sipprivate.feature.command.impl.Taco.Frame frame = me.x150.sipprivate.feature.command.impl.Taco.getCurrentFrame();
        if (frame == null) {
            FontRenderers.getMono().drawString(stack, "Nothing to taco", 0, 0, 0xFFFFFF);
            return;
        }
        Texture current = frame.getI();

        RenderSystem.disableBlend();
        RenderSystem.setShaderTexture(0, current);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        DrawableHelper.drawTexture(stack, 0, 0, 0, 0, 0, (int) width, (int) height, (int) width, (int) height);
    }
}
