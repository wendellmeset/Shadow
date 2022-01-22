package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.feature.gui.widget.RoundButton;
import me.x150.sipprivate.feature.gui.widget.RoundTextFieldWidget;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.render.MSAAFramebuffer;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class ProxyManagerScreen extends ClientScreen {
    public static Proxy currentProxy = null;
    static double widgetWidth = 300;
    static double widgetHeight = 400;
    static ClientFontRenderer title = FontRenderers.getCustomNormal(40);
    static boolean isSocks4 = false;
    Screen parent;
    RoundTextFieldWidget ip, port;
    RoundButton reset, apply, type;

    public ProxyManagerScreen(Screen parent) {
        super(MSAAFramebuffer.MAX_SAMPLES);
        this.parent = parent;
    }

    double padding() {
        return 5;
    }

    @Override
    protected void init() {

        RoundButton exit = new RoundButton(new Color(40, 40, 40), width - 20 - 5, 5, 20, 20, "X", this::onClose);
        addDrawableChild(exit);

        double wWidth = widgetWidth - padding() * 2d;
        double sourceX = width / 2d - widgetWidth / 2d + wWidth / 2d;
        double sourceY = height / 2d - widgetHeight / 2d;
        double yOffset = padding() + title.getMarginHeight() + FontRenderers.getNormal().getMarginHeight() + padding();
        ip = new RoundTextFieldWidget(sourceX, sourceY + yOffset, wWidth, 20, "IP", 10);
        yOffset += ip.getHeight() + padding();
        port = new RoundTextFieldWidget(sourceX, sourceY + yOffset, wWidth, 20, "Port", 10);
        if (currentProxy != null) {
            ip.setText(currentProxy.address);
            port.setText(currentProxy.port + "");
        }
        yOffset += port.getHeight() + padding();
        type = new RoundButton(new Color(40, 40, 40), sourceX, sourceY + yOffset, wWidth, 20, "Type: " + (isSocks4 ? "Socks4" : "Socks5"), () -> {
            isSocks4 = !isSocks4;
            type.setText("Type: " + (isSocks4 ? "Socks4" : "Socks5"));
        });
        yOffset += 20 + padding();
        double doubleWidth = wWidth / 2d - padding() / 2d;
        reset = new RoundButton(new Color(40, 40, 40), sourceX, yOffset, doubleWidth, 20, "Reset", () -> {
            currentProxy = null;
            ip.set("");
            port.set("");
        });
        apply = new RoundButton(new Color(40, 40, 40), sourceX + doubleWidth + padding(), yOffset, doubleWidth, 20, "Apply", () -> {
            currentProxy = new Proxy(ip.get(), Integer.parseInt(port.get()), isSocks4);
        });

        addDrawableChild(ip);
        addDrawableChild(port);
        addDrawableChild(type);
        addDrawableChild(reset);
        addDrawableChild(apply);


        super.init();
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        if (parent != null) parent.render(stack, mouseX, mouseY, delta);
        Renderer.R2D.renderQuad(stack, new Color(0, 0, 0, 130), 0, 0, width, height);
        double wWidth = widgetWidth - padding() * 2d;
        double sourceX = width / 2d - widgetWidth / 2d + padding();
        double sourceY = height / 2d - widgetHeight / 2d;
        double actualSourceX = width / 2d - widgetWidth / 2d;
        double yOffset = padding();
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20), width / 2d - widgetWidth / 2d, height / 2d - widgetHeight / 2d, width / 2d + widgetWidth / 2d, height / 2d + widgetHeight / 2d, 10, 20);
        title.drawString(stack, "Proxies", (float) (actualSourceX + padding()), (float) (sourceY + yOffset), 0xFFFFFF, false);
        yOffset += title.getMarginHeight();
        String t = "Manage your proxy connection";
        FontRenderers.getNormal().drawString(stack, t, (float) (actualSourceX + padding()), (float) (sourceY + yOffset), 0xFFFFFF, false);
        if (currentProxy != null) {
            String text = "Connected: " + currentProxy.address + ":" + currentProxy.port;
            double textWidth = FontRenderers.getNormal().getStringWidth(text);
            FontRenderers.getNormal().drawString(stack, text, (float) (actualSourceX + widgetWidth - padding() - textWidth), (float) (sourceY + yOffset), 0xFFFFFF, false);
        }
        yOffset += FontRenderers.getNormal().getMarginHeight() + padding();

        ip.setX(sourceX);
        ip.setY(sourceY + yOffset);
        yOffset += ip.getHeight() + padding();
        port.setX(sourceX);
        port.setY(sourceY + yOffset);
        yOffset += port.getHeight() + padding();
        type.setX(sourceX);
        type.setY(sourceY + yOffset);
        yOffset += 20 + padding();
        double doubleWidth = wWidth / 2d - padding() / 2d;
        reset.setX(sourceX);
        reset.setY(sourceY + yOffset);
        apply.setX(sourceX + doubleWidth + padding());
        apply.setY(sourceY + yOffset);
        apply.setEnabled(canApply());
        yOffset += 20 + padding();
        widgetHeight = yOffset;

        super.renderInternal(stack, mouseX, mouseY, delta);
    }

    boolean canApply() {
        String currentIp = this.ip.get();
        if (currentIp.isEmpty()) return false;
        String currentPort = this.port.get();
        try {
            int port = Integer.parseInt(currentPort);
            if (port < 0 || port > 0xFFFF) return false;
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            child.mouseClicked(0, 0, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        client.setScreen(parent);
    }

    public record Proxy(String address, int port, boolean socks4) {

    }
}
