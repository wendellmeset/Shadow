package me.x150.sipprivate.helper.font.adapter.impl;

import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import me.x150.sipprivate.helper.font.render.GlyphPageFontRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ClientFontRenderer implements FontAdapter {
    final GlyphPageFontRenderer renderer;

    public ClientFontRenderer(GlyphPageFontRenderer fontRenderer) {
        this.renderer = fontRenderer;
    }

    @Override
    public int drawString(MatrixStack matrices, String text, float x, float y, int color) {
        return renderer.drawString(matrices, text, x, y, color);
    }

    @Override
    public int drawString(MatrixStack matrices, String text, double x, double y, int color) {
        return renderer.drawString(matrices, text, x, y, color);
    }

    @Override
    public int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        return renderer.drawCenteredString(matrices, text, x, y, color);
    }

    @Override
    public float getStringWidth(String text) {
        return renderer.getStringWidth(text);
    }

    @Override
    public float getFontHeight() {
        return renderer.getFontHeight();
    }

    @Override
    public float getMarginHeight() {
        return getFontHeight();
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        renderer.drawString(matrices, s, x, y, color, dropShadow);
    }

    @Override
    public String trimStringToWidth(String in, double width) {
        return renderer.trimStringToWidth(in, width);
    }

    @Override
    public String trimStringToWidth(String in, double width, boolean reverse) {
        return renderer.trimStringToWidth(in, width, reverse);
    }

    public int getSize() {
        return renderer.size;
    }
}
