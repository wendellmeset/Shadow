package me.x150.coffee.helper.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Stack;

public class ClipStack {
    public static final ClipStack globalInstance = new ClipStack();
    Stack<Rectangle> clipStack = new Stack<>();

    //Rectangle lastStack = null;
    public void addWindow(MatrixStack stack, Rectangle r) {
        if (clipStack.empty()) {
            clipStack.push(r);
            Renderer.R2D.beginScissor(stack, r.getX(), r.getY(), r.getX1(), r.getY1());
        } else {
            Rectangle lastClip = clipStack.peek();
            double lsx = lastClip.getX();
            double lsy = lastClip.getY();
            double lstx = lastClip.getX1();
            double lsty = lastClip.getY1();
            double nsx = MathHelper.clamp(r.getX(), lsx, lstx);
            double nsy = MathHelper.clamp(r.getY(), lsy, lsty);
            double nstx = MathHelper.clamp(r.getX1(), nsx, lstx);
            double nsty = MathHelper.clamp(r.getY1(), nsy, lsty); // totally intended varname
            clipStack.push(new Rectangle(nsx, nsy, nstx, nsty));
            Renderer.R2D.beginScissor(stack, nsx, nsy, nstx, nsty);
        }
    }

    public void popWindow(MatrixStack stack) {
        clipStack.pop();
        if (clipStack.empty()) {
            Renderer.R2D.endScissor();
        } else {
            Rectangle r = clipStack.peek();
            Renderer.R2D.beginScissor(stack, r.getX(), r.getY(), r.getX1(), r.getY1());
        }
    }

    public void renderOutsideClipStack(MatrixStack stack, Runnable e) {
        if (clipStack.empty()) {
            e.run();
        } else {
            Renderer.R2D.endScissor();
            e.run();
            Rectangle r = clipStack.peek();
            Renderer.R2D.beginScissor(stack, r.getX(), r.getY(), r.getX1(), r.getY1());
        }
    }
}
