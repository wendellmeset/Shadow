package net.shadow.client.feature.gui.backdoor;

import java.awt.Color;

import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.render.Renderer;

public class BackdoorEntry {
    
    double x, y, width, height;
    int id;
    public String canonicalid;
    String ip, motd;
    Color flipflop = new Color(25, 25, 25, 255);

    public BackdoorEntry(double x, double y, double w, double h, int id, String cid, String ip, String motd) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.id = id;
        this.canonicalid = cid;
        this.ip = ip;
        this.motd = motd;
    }

    public void render(MatrixStack matrices) {
        Renderer.R2D.renderRoundedQuad(matrices, flipflop, x, y, x + width, y + height, 5, 10);
        FontRenderers.getRenderer().drawString(matrices, ip, x + 5, y + 5, 0xFFFFFF);
        FontRenderers.getRenderer().drawString(matrices, motd, x + 5, y + 25, 0xFFFFFF);
    }

    public boolean inBounds(double cx, double cy) {
        return cx >= x && cx < x + width && cy >= y && cy < y + height;
    }


    public boolean clicked(double x, double y, int button, BackdoorScreen callback){
        if(inBounds(x, y)){
            callback.postMessage(this.id);
            System.out.println("sus");
        }
        return false;
    }

    public void postMessage(String cmd){
        switch(cmd){
            case "sel" -> {
                flipflop = new Color(15, 15, 15, 255);
            }

            case "desel" -> {
                flipflop = new Color(25, 25, 25, 255);
            }
        }
    }


    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
