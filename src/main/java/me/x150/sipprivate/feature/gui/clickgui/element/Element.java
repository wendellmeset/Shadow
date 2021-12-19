package me.x150.sipprivate.feature.gui.clickgui.element;

import net.minecraft.client.util.math.MatrixStack;

public abstract class Element {
    public double x, y, width, height;

    public Element(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public boolean inBounds(double cx, double cy) {
        return cx >= x && cx <= x + width && cy >= y && cy <= y + height;
    }

    abstract public boolean clicked(double x, double y, int button);

    abstract public boolean dragged(double x, double y, double deltaX, double deltaY);

    abstract public boolean released();

    abstract public boolean keyPressed(int keycode);

    abstract public void render(MatrixStack matrices);

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
