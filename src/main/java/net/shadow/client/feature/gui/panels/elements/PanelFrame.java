package net.shadow.client.feature.gui.panels.elements;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.shadow.client.feature.gui.clickgui.element.Element;
import net.shadow.client.feature.gui.clickgui.theme.ThemeManager;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Transitions;
import java.util.Set;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;

public class PanelFrame extends Element {
    String title;
    Element[] elements;
    HashMap<Element, double[]> positions = new HashMap<Element, double[]>();
    boolean selected = false;
    boolean open = true;
    double xGoal, yGoal;
    double smoothTransition = 0;
    double realglobal;
    boolean resizer = false;

    public PanelFrame(double x, double y, double w, double h, String title, Element[] elements) {
        super(x, y, w, h);
        this.title = title;
        this.elements = elements;
        this.xGoal = x;
        this.yGoal = y;
        for(Element el : this.elements){
            positions.put(el, new double[]{el.getX(), el.getY(), el.getWidth(), el.getHeight()});
        }
    }

    @Override
    public boolean clicked(double x, double y, int button) {
        if(x >= this.x  + this.width - 15 && x < this.x + this.width && y >= this.y + (realglobal * height) - 15&& y < this.y + (realglobal * height)){
            System.out.println("real");
            resizer = true;
            return false;
        }
        if (x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + 15) {
            if (button == 0) {
                selected = true;
                return false;
            } else if (button == 1) {
                open = !open;
            }
        }
        for(Element pb : elements){
            pb.clicked(x, y, button);
        }
        return false;
    }

    @Override
    public boolean dragged(double x, double y, double deltaX, double deltaY, int button) {
        if(selected){
            this.xGoal += deltaX;
            this.yGoal += deltaY;
        }
        if(resizer){
            this.width += deltaX;
            this.height += deltaY;
        }
        for(Element pb : elements){
            pb.dragged(x, y, deltaX, deltaY, button);
        }
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean released() {
        selected = false;
        resizer = false;
        for(Element pb : elements){
            pb.released();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keycode, int modifiers) {
        for(Element pb : elements){
            pb.keyPressed(keycode, modifiers);
        }
        // TODO Auto-generated method stub
        return false;
    }

    double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    @Override
    public void render(MatrixStack matrices, double mouseX, double mouseY, double scrollBeingUsed) {
        this.x = Transitions.transition(this.x, this.xGoal, 4);
        this.y =  Transitions.transition(this.y, this.yGoal, 4);
        double d = 0.03;
        if(open) {
            d *= -1;
        }
        smoothTransition += d;
        smoothTransition = MathHelper.clamp(smoothTransition, 0, 1);
        double real = easeInOutQuint(smoothTransition);
        if(real > 0.09){
            Renderer.R2D.renderRoundedQuad(matrices, ThemeManager.getMainTheme().getInactive(), x, y, x + width, y + (real * height), 5, 10);
            realglobal = real;   
            Renderer.R2D.renderRoundedQuad(matrices, ThemeManager.getMainTheme().getConfig(), x + width - 15, y + (real * height) - 15, x + width, y + (real * height), 5, 10);
        }
        Renderer.R2D.beginScissor(x, y, x + width, y + (real * width));
        for(Element pb : elements){
            pb.setX(this.x + positions.get(pb)[0] + 5);
            pb.setY(this.y + 15 + positions.get(pb)[1]);
            if(positions.get(pb)[2] < 0){
                pb.setWidth(this.width - 10);
            }
            if(positions.get(pb)[3] < 0){
                pb.setHeight(this.height - 10);
            }
        }
        for(Element pb : elements){
            pb.render(matrices, mouseX, mouseY, scrollBeingUsed);
        }
        Renderer.R2D.endScissor();
        Renderer.R2D.renderRoundedQuad(matrices, ThemeManager.getMainTheme().getHeader(), x, y, x + width, y + 15, 5, 10);
        FontRenderers.getRenderer().drawString(matrices, title, x  + (width/2) - FontRenderers.getRenderer().getStringWidth(title) / 2, y + 3, new Color(255, 255, 255, 255).getRGB());
        // TODO Auto-generated method stub
        
    }


    @Override
    public void tickAnim() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean charTyped(char c, int mods) {
        for(Element pb : elements){
            pb.charTyped(c, mods);
        }
        // TODO Auto-generated method stub
        return false;
    }

}
