/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.gui.screen;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.gui.FastTickable;
import net.shadow.client.feature.gui.notifications.hudNotif.HudNotification;
import net.shadow.client.feature.gui.widget.RoundButton;
import net.shadow.client.feature.gui.widget.RoundTextFieldWidget;
import net.shadow.client.helper.IRCWebSocket;
import net.shadow.client.helper.ShadowAPIWrapper;
import net.shadow.client.helper.font.FontRenderers;
import net.shadow.client.helper.font.adapter.impl.BruhAdapter;
import net.shadow.client.helper.render.ClipStack;
import net.shadow.client.helper.render.Rectangle;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.render.Scroller;
import net.shadow.client.helper.ws.SimpleWebsocket;

import java.awt.Color;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class OnlineServicesDashboardScreen extends ClientScreen implements FastTickable {
    static List<LogsFieldWidget.LogEntry> logs = new CopyOnWriteArrayList<>();
    private static OnlineServicesDashboardScreen instance;
    long reconnectTime = System.currentTimeMillis();
    SimpleWebsocket logsSocket;
    AccountList dvw;

    private OnlineServicesDashboardScreen() {

    }

    public static OnlineServicesDashboardScreen getInstance() {
        if (instance == null) instance = new OnlineServicesDashboardScreen();
        return instance;
    }

    void initSocket() {
        if (ShadowAPIWrapper.getAuthKey() != null) {
            logs.clear();
            logsSocket = new SimpleWebsocket(URI.create(ShadowAPIWrapper.BASE_WS + "/admin/logs"), Map.of("Authorization", ShadowAPIWrapper.getAuthKey()), () -> {
                HudNotification.create("Websocket disconnected, reconnecting in 3 seconds", 3000, HudNotification.Type.INFO);
                reconnectTime = System.currentTimeMillis() + Duration.ofSeconds(3).toMillis();
                logs.clear();
            }, this::socketMessageRecieved);
            logsSocket.connect();
        }
    }

    void socketMessageRecieved(String msg) {
        IRCWebSocket.Packet pack = new Gson().fromJson(msg, IRCWebSocket.Packet.class);
        if (pack.id.equals("log")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MM HH:mm:ss");
            LinkedHashMap<String, String> props = new LinkedHashMap<>();
            props.put("Time", sdf.format(pack.data.get("time")));
            props.put("Severity", pack.data.get("severity").toString());
            logs.add(0, new LogsFieldWidget.LogEntry(props, pack.data.get("message").toString(), switch (pack.data.get("severity").toString()) {
                case "WARNING" -> Color.YELLOW.getRGB();
                case "SEVERE" -> new Color(255, 50, 50).getRGB();
                default -> 0xFFFFFF;
            }));
        }
    }

    void populateAccountList() {
        dvw.aww.clear();
        double yO = 0;
        for (ShadowAPIWrapper.AccountEntry account : ShadowAPIWrapper.getAccounts()) {
            AccountViewerWidget avw = new AccountViewerWidget(account.username, account.password, 0, yO, 300, 30, () -> {
                if (ShadowAPIWrapper.deleteAccount(account.username, account.password)) this.populateAccountList();
            });
            yO += avw.height + 5;
            dvw.add(avw);
        }
        dvw.add(new RegisterAccountViewer(0, yO, 300, 30, (s, s2) -> {
            if (ShadowAPIWrapper.registerAccount(s, s2)) populateAccountList();
        }));
    }

    @Override
    protected void init() {
        addDrawableChild(new LogsFieldWidget(5, 5, width - 10, height / 2d - 5, OnlineServicesDashboardScreen.logs));
        dvw = new AccountList(5, height / 2d + 5, 300, height / 2d - 10);
        populateAccountList();
        addDrawableChild(dvw);
    }

    @Override
    public void onFastTick() {
        if (reconnectTime != -1 && reconnectTime < System.currentTimeMillis()) {
            initSocket();
            reconnectTime = -1;
        }
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        renderBackground(stack);
        super.renderInternal(stack, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Element child : this.children()) {
            if (child.mouseScrolled(mouseX, mouseY, amount)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Element child : this.children()) {
            if (child.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : this.children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (Element child : this.children()) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    static class RegisterAccountViewer extends AccountViewerWidget {
        BiConsumer<String, String> r;
        RoundTextFieldWidget user, pass;
        RoundButton reg;

        public RegisterAccountViewer(double x, double y, double width, double height, BiConsumer<String, String> onReg) {
            super("", "", x, y, width, height, () -> {
            });
            this.r = onReg;
        }

        Element[] getEl() {
            return new Element[]{user, pass, reg};
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (user == null || pass == null || reg == null) initWidgets();
            Renderer.R2D.renderRoundedQuad(matrices, new Color(10, 10, 20), x, y, x + width, y + height, 5, 20);
            this.user.render(matrices, mouseX, mouseY, delta);
            this.pass.render(matrices, mouseX, mouseY, delta);
            this.reg.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void onFastTick() {
            this.reg.onFastTick();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Element element : getEl()) {
                element.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            for (Element element : getEl()) {
                if (element.charTyped(chr, modifiers)) return true;
            }
            return super.charTyped(chr, modifiers);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            for (Element element : getEl()) {
                if (element.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        void initWidgets() {
            double h = 20;
            double pad = (this.height - h) / 2d;
            double regBtnWidth = 60;
            double oneWidth = (this.width - regBtnWidth - pad * 3) / 2d - 2.5;
            this.user = new RoundTextFieldWidget(x + pad, y + this.height / 2d - h / 2d, oneWidth, h, "Username");
            this.pass = new RoundTextFieldWidget(x + pad + oneWidth + 5, y + this.height / 2d - h / 2d, oneWidth, h, "Password");
            this.reg = new RoundButton(RoundButton.STANDARD, x + this.width - pad - regBtnWidth, y + this.height / 2d - h / 2d, regBtnWidth, h, "Register", () -> {
                this.r.accept(this.user.get(), this.pass.get());
                this.user.set("");
                this.pass.set("");
            });
        }
    }

    @RequiredArgsConstructor
    static
    class AccountViewerWidget implements Element, Drawable, Selectable, FastTickable {
        final String username, password;
        final double x, y, width, height;
        final Runnable onDelete;
        RoundButton deleteBtn = null;

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (deleteBtn == null)
                deleteBtn = new RoundButton(RoundButton.STANDARD, x + width - (height - 20) / 2d - 60, y + (height - 20) / 2d, 60, 20, "Delete", onDelete);
            Renderer.R2D.renderRoundedQuad(matrices, new Color(10, 10, 20), x, y, x + width, y + height, 5, 20);
            double h = FontRenderers.getRenderer().getFontHeight();
            double pad = height - h;

            FontRenderers.getRenderer().drawString(matrices, username + ":" + password, x + pad / 2d, y + height / 2d - h / 2d, 0xFFFFFF);
            deleteBtn.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public SelectionType getType() {
            return null;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onFastTick() {
            if (deleteBtn != null) deleteBtn.onFastTick();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return deleteBtn != null && deleteBtn.mouseClicked(mouseX, mouseY, button);
        }
    }

    @RequiredArgsConstructor
    class LogsFieldWidget implements Element, Drawable, Selectable, FastTickable {
        final double x, y, w, h;
        final List<LogEntry> logs;
        Scroller scroller = new Scroller(0);

        double heightPerLine() {
            return FontRenderers.getRenderer().getFontHeight() + 8;
        }

        double contentHeight() {
            return heightPerLine() + logs.size() * heightPerLine();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            double yOffset = heightPerLine();
            Renderer.R2D.renderRoundedQuad(matrices, new Color(10, 10, 20), x, y, x + w, y + h, 5, 20);
            if (logs.isEmpty()) {
                BruhAdapter ba = FontRenderers.getCustomSize(40);
                ba.drawCenteredString(matrices, "No logs yet", x + w / 2d, y + h / 2d - ba.getFontHeight() / 2d, 1f, 1f, 1f, 0.3f);
                return;
            }
            List<Bruh> bruhs = new ArrayList<>();
            for (LogEntry log : logs) {
                log.additionalProps.forEach((s, s2) -> {
                    if (bruhs.stream().noneMatch(bruh -> bruh.content.equals(s))) {
                        bruhs.add(new Bruh(s, Math.max(FontRenderers.getRenderer().getStringWidth(s2), FontRenderers.getRenderer().getStringWidth(s))));
                    } else {
                        Bruh b = bruhs.stream().filter(bruh -> bruh.content.equals(s)).findFirst().orElseThrow();
                        double w = Math.max(b.width, Math.max(FontRenderers.getRenderer().getStringWidth(s2), FontRenderers.getRenderer().getStringWidth(s)));
                        bruhs.remove(b);
                        bruhs.add(new Bruh(s, w));
                    }
                });
            }
            double xOffset = 4;
            for (Bruh bruh : bruhs) {
                FontRenderers.getRenderer().drawString(matrices, bruh.content, x + xOffset, y + heightPerLine() / 2d - FontRenderers.getRenderer().getFontHeight() / 2d, 0xBBBBBB);
                xOffset += bruh.width + 7;
            }
            Renderer.R2D.renderQuad(matrices, Color.WHITE, x, y + yOffset, x + w, y + yOffset + 1);
            ClipStack.globalInstance.addWindow(matrices, new Rectangle(x, y + yOffset, x + w, y + h));
            matrices.push();
            matrices.translate(0, scroller.getScroll(), 0);
            for (LogEntry log : logs) {
                double finalYOffset = yOffset;
                log.additionalProps.forEach((s, s2) -> {
                    int index = bruhs.indexOf(new Bruh(s, 0));
                    double xOffsetToConsider = 4;
                    for (int i = 0; i < index; i++) {
                        xOffsetToConsider += bruhs.get(i).width + 7;
                    }
                    FontRenderers.getRenderer().drawString(matrices, s2, x + xOffsetToConsider, y + finalYOffset + heightPerLine() / 2d - FontRenderers.getRenderer().getFontHeight() / 2d, 0xFFFFFF);
                });
                double xO = bruhs.stream().map(bruh -> bruh.width + 7).reduce(Double::sum).orElse(0d) + 4;
                FontRenderers.getRenderer().drawString(matrices, log.msg, x + xO, y + yOffset + heightPerLine() / 2d - FontRenderers.getRenderer().getFontHeight() / 2d, 0xFFFFFF);
                yOffset += heightPerLine();
            }
            matrices.pop();
            ClipStack.globalInstance.popWindow();
        }

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onFastTick() {
            scroller.tick();
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                double contentHeight = contentHeight();
                double elScroll = contentHeight - h;
                scroller.setBounds(0, elScroll);
                scroller.scroll(amount);
            }
            return Element.super.mouseScrolled(mouseX, mouseY, amount);
        }

        public record LogEntry(Map<String, String> additionalProps, String msg, int color) {

        }

        record Bruh(String content, double width) {
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Bruh bruh = (Bruh) o;
                return Objects.equals(content, bruh.content);
            }

            @Override
            public int hashCode() {
                return Objects.hash(content, width);
            }
        }
    }

    @RequiredArgsConstructor
    class AccountList implements Element, Drawable, Selectable, FastTickable {
        final double x, y, w, h;
        @Getter
        List<AccountViewerWidget> aww = new CopyOnWriteArrayList<>();
        Scroller s = new Scroller(0);

        public void add(AccountViewerWidget v) {
            aww.add(v);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            ClipStack.globalInstance.addWindow(matrices, new Rectangle(x, y, x + w, y + h));
            matrices.translate(x, y + s.getScroll(), 0);
            for (Drawable drawable : aww) {
                drawable.render(matrices, (int) (mouseX - x), (int) (mouseY - y - s.getScroll()), delta);
            }
            ClipStack.globalInstance.popWindow();
            matrices.pop();
        }

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onFastTick() {
            s.tick();
            for (Element element : aww) {
                if (element instanceof FastTickable ft) ft.onFastTick();
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (Element element : aww) {
                if (element.mouseClicked(mouseX - x, mouseY - y - s.getScroll(), button)) return true;
            }
            return Element.super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            for (Element element : aww) {
                if (element.mouseReleased(mouseX - x, mouseY - y - s.getScroll(), button)) return true;
            }
            return Element.super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            for (Element element : aww) {
                if (element.mouseDragged(mouseX - x, mouseY - y, button, deltaX, deltaY)) return true;
            }
            return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            double he = 0;
            for (AccountViewerWidget accountViewerWidget : aww) {
                he = Math.max(he, accountViewerWidget.y + accountViewerWidget.height);
            }
            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                double elScroll = he - h;
                s.setBounds(0, elScroll);
                s.scroll(amount);
            }
            return Element.super.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            for (Element element : aww) {
                if (element.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
            return Element.super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            for (Element element : aww) {
                if (element.keyReleased(keyCode, scanCode, modifiers)) return true;
            }
            return Element.super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            for (Element element : aww) {
                if (element.charTyped(chr, modifiers)) return true;
            }
            return Element.super.charTyped(chr, modifiers);
        }
    }
}
