package me.x150.coffee.helper.font.adapter;

import net.minecraft.client.util.math.MatrixStack;

public interface FontAdapter {
    int drawString(MatrixStack matrices, String text, float x, float y, int color);

    int drawString(MatrixStack matrices, String text, double x, double y, int color);

    int drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a);

    int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color);

    int drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a);

    float getStringWidth(String text);

    float getFontHeight();

    float getFontHeight(String text);

    float getMarginHeight();

    void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow);

    void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow);

    String trimStringToWidth(String in, double width);

    String trimStringToWidth(String in, double width, boolean reverse);
}
