package net.shadow.client.feature.gui.panels;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.gui.panels.elements.PanelFrame;
import net.shadow.client.helper.render.MSAAFramebuffer;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PanelsGui extends Screen{
    PanelFrame[] renders; 

    public PanelsGui(PanelFrame[] renders) {
        super(Text.of(""));
        this.renders = renders;
    }

    @Override
    protected void init() {
        
    }
    

    @Override
    public void close() {
        ShadowMain.client.setScreen(null);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        
        for(PanelFrame pf : renders){
            pf.scroll(mouseX, mouseY, amount);
        }
        return true;
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            for(PanelFrame pf : renders){
                pf.render(matrices, mouseX, mouseY, delta);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(PanelFrame pf : renders){
            pf.clicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(PanelFrame pf : renders){
            pf.released();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for(PanelFrame pf : renders){
            pf.dragged(mouseX, mouseY, deltaX, deltaY, button);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for(PanelFrame pf : renders){
            pf.keyPressed(keyCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for(PanelFrame pf : renders){
            pf.charTyped(chr, modifiers);
        }
        return false;
    }
}
