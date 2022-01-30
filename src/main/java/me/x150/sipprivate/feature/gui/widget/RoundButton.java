package me.x150.sipprivate.feature.gui.widget;

import me.x150.sipprivate.feature.gui.DoesMSAA;
import me.x150.sipprivate.feature.gui.FastTickable;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class RoundButton implements Element, Drawable, Selectable, FastTickable, DoesMSAA {

    public static Color STANDARD = new Color(40, 40, 40);

    final Runnable onPress;
    final Color color;
    String text;
    double x, y, width, height;
    double animProgress = 0;
    boolean isHovered = false;
    boolean enabled = true;

    public RoundButton(Color color, double x, double y, double w, double h, String t, Runnable a) {
        this.onPress = a;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.text = t;
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void onFastTick() {
        double d = 0.04;
        if (!isHovered) {
            d *= -1;
        }
        animProgress += d;
        animProgress = MathHelper.clamp(animProgress, 0, 1);

    }

    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    boolean inBounds(double cx, double cy) {
        return cx >= x && cx < x + width && cy >= y && cy < y + height;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        isHovered = inBounds(mouseX, mouseY) && isEnabled();
        matrices.push();
        matrices.translate(x + width / 2d, y + height / 2d, 0);
        float animProgress = (float) easeInOutQuint(this.animProgress);
        matrices.scale(MathHelper.lerp(animProgress, 1f, 0.95f), MathHelper.lerp(animProgress, 1f, 0.95f), 1f);
        double originX = -width / 2d;
        double originY = -height / 2d;
        Renderer.R2D.renderRoundedQuad(matrices, color, originX, originY, width / 2d, height / 2d, 10, 10);
        FontRenderers.getNormal().drawString(matrices, text, -(FontRenderers.getNormal().getStringWidth(text) + 2) / 2f, -FontRenderers.getNormal().getMarginHeight() / 2f, isEnabled() ? 0xFFFFFF : 0xAAAAAA, false);
        matrices.pop();
    }

    @Override
    public SelectionType getType() {
        return isHovered ? SelectionType.HOVERED : SelectionType.NONE;
    }


    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inBounds(mouseX, mouseY) && isEnabled() && button == 0) {
            onPress.run();
            return true;
        }
        return false;
    }
}
