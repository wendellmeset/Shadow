package me.x150.coffee.feature.gui.screen;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.gui.FastTickable;
import me.x150.coffee.feature.gui.notifications.Notification;
import me.x150.coffee.feature.gui.widget.RoundButton;
import me.x150.coffee.helper.NbtFormatter;
import me.x150.coffee.helper.font.FontRenderers;
import me.x150.coffee.helper.render.Renderer;
import me.x150.coffee.helper.util.Transitions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.compress.utils.Lists;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NbtEditorScreen extends ClientScreen implements FastTickable {
    ItemStack stack;
    List<String> initial = new ArrayList<>();
    boolean inString = false;
    boolean inSingleString = false;
    char[] suffixes = {
            'b', 's', 'L', 'f', 'd'
    };
    int editorX = 0;
    int editorY = 0;
    double scrollX = 0;
    double smoothScrollX = 0;
    double scroll = 0;
    double smoothScroll = 0;

    public NbtEditorScreen(ItemStack stack) {
        super();
        this.stack = stack;
        NbtCompound compound = this.stack.getOrCreateNbt();
        NbtFormatter.RGBColorText formatted = new NbtFormatter("  ", 0, Lists.newArrayList()).apply(compound);
        StringBuilder current = new StringBuilder();
        for (NbtFormatter.RGBColorText.RGBEntry entry : formatted.getEntries()) {
            if (entry == NbtFormatter.RGBColorText.NEWLINE) {
                initial.add(current.toString());
                current = new StringBuilder();
            } else current.append(entry.value());
        }
        initial.add(current.toString());
    }

    boolean isNumber(String t) {
        try {
            Long.parseLong(t);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    void format() {
        String nbtString = String.join("\n", initial);
        try {
            NbtCompound nc = StringNbtReader.parse(nbtString);
            NbtFormatter.RGBColorText formatted = new NbtFormatter("  ", 0, Lists.newArrayList()).apply(nc);
            StringBuilder current = new StringBuilder();
            initial.clear();
            for (NbtFormatter.RGBColorText.RGBEntry entry : formatted.getEntries()) {
                if (entry == NbtFormatter.RGBColorText.NEWLINE) {
                    initial.add(current.toString());
                    current = new StringBuilder();
                } else current.append(entry.value());
            }
            initial.add(current.toString());
        } catch (Exception e) {
            client.setScreen(new NotificationScreen(this, "Invalid JSON!", Notification.Type.ERROR));
        }
    }

    void save() {
        String nbtString = String.join("\n", initial);
        try {
            NbtCompound nc = StringNbtReader.parse(nbtString);
            this.stack.setNbt(nc);
            this.onClose();
        } catch (Exception e) {
            client.setScreen(new NotificationScreen(this, "Invalid JSON!", Notification.Type.ERROR));
        }
    }

    @Override
    protected void init() {
        RoundButton format = new RoundButton(RoundButton.STANDARD, 5, height - 30 + 5, 64, 20, "Format", this::format);
        addDrawableChild(format);
        RoundButton save = new RoundButton(RoundButton.STANDARD, 5 + format.getWidth() + 5, height - 30 + 5, 64, 20, "Save", this::save);
        addDrawableChild(save);
    }

    int getColor(String total, int index, char c) {
        if (c == '"') {
            if (total.indexOf('"', index + 1) == -1 && !inString) return 0xFF5555;
            inString = !inString;
        }
        if (c == '\'') {
            if (total.indexOf('\'', index + 1) == -1 && !inSingleString) return 0xFF5555;
            inSingleString = !inSingleString;
        }
        if (inString || inSingleString || c == '"' || c == '\'') return 0x55FF55;
        // if the index of the next : from where we are right now is smaller than the index of the last , or the last , is beyond where we are now
        // and the next : from where we are right now is beyond where we are right now, mark it
        if ((total.indexOf(':', index) < total.lastIndexOf(',') || total.lastIndexOf(',') < index)
                && total.indexOf(' ', index) > total.indexOf(':', index)
                && total.indexOf(':', index) > index) {
            return 0x55FFFF;
        }
        boolean isSuffix = false;
        for (char suffix : suffixes) {
            if (suffix == c) {
                isSuffix = true;
                break;
            }
        }
        if (isSuffix && index > 0 && isNumber(total.charAt(index - 1) + "")) {
            return 0xFF5555;
        }
        return 0xFFFFFF;
    }

    @Override
    public void onFastTick() {
        this.smoothScroll = Transitions.transition(smoothScroll, scroll, 7, 0);
        this.smoothScrollX = Transitions.transition(smoothScrollX, scrollX, 7, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double contentWidth = initial.stream().map(s -> FontRenderers.getMono().getStringWidth(s)).max(Comparator.comparingDouble(value -> value)).orElse(0f);
        double windowWidth = width - 14;
        double entitledX = contentWidth - windowWidth;
        entitledX = Math.max(0, entitledX);

        double contentHeight = initial.size() * FontRenderers.getMono().getMarginHeight();
        double windowHeight = height - 37; // calc padding
        double entitledScroll = contentHeight - windowHeight;
        entitledScroll = Math.max(0, entitledScroll);

        if (InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            scrollX -= amount * 10;
        } else {
            scroll -= amount * 10;
        }
        scrollX = MathHelper.clamp(scrollX, 0, entitledX);
        scroll = MathHelper.clamp(scroll, 0, entitledScroll);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    double getEditorXPosition() {
        String i = initial.get(editorY);
        return 7 + FontRenderers.getMono().getStringWidth(i.substring(0, editorX)) - smoothScrollX;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_RIGHT -> editorX += 1;
            case GLFW.GLFW_KEY_LEFT -> editorX -= 1;
            case GLFW.GLFW_KEY_UP -> editorY -= 1;
            case GLFW.GLFW_KEY_DOWN -> editorY += 1;
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (editorX == 0 && editorY == 0) {
                    break;
                }
                String index = initial.get(editorY);
                if (editorX > 0) {
                    StringBuilder sb = new StringBuilder(index);
                    sb.deleteCharAt(editorX - 1);
                    initial.set(editorY, sb.toString());
                    editorX--;
                } else {
                    initial.remove(editorY);
                    editorY--;
                    String b = initial.get(editorY);
                    editorX = b.length();
                    initial.set(editorY, b + index);
                }
            }
            case GLFW.GLFW_KEY_DELETE -> {
                String index = initial.get(editorY);
                if (editorY == initial.size() - 1 && editorX == index.length()) break;
                if (editorX < index.length()) {
                    StringBuilder sb = new StringBuilder(index);
                    sb.deleteCharAt(editorX);
                    initial.set(editorY, sb.toString());
                } else {
                    initial.remove(editorY);
                    String b = initial.get(editorY + 1);
                    initial.set(editorY, index + b);
                }
            }
            case GLFW.GLFW_KEY_END -> {
                String index = initial.get(editorY);
                editorX = index.length();
            }
            case GLFW.GLFW_KEY_HOME -> editorX = 0;
            case GLFW.GLFW_KEY_ENTER -> {
                String index = initial.get(editorY);
                int leadingSpaces = 0;
                for (char c : index.toCharArray()) {
                    if (c != ' ') break;
                    leadingSpaces++;
                }
                if (editorX < index.length()) {
                    String overtake = index.substring(editorX);
                    initial.set(editorY, index.substring(0, editorX));
                    editorY++;
                    initial.add(editorY, " ".repeat(leadingSpaces) + overtake);
                } else {
                    if (!index.trim().isEmpty() && !index.startsWith(",")) {
                        index = index + ",";
                    }
                    initial.set(editorY, index);
                    editorY++;
                    initial.add(editorY, " ".repeat(leadingSpaces));
                }
                editorX = leadingSpaces;
            }
        }
        editorY = MathHelper.clamp(editorY, 0, initial.size() - 1);
        String index = initial.get(editorY);
        editorX = MathHelper.clamp(editorX, 0, index.length());
        if (getEditorXPosition() < 7) {
            scrollX += getEditorXPosition() - 7;
        } else if (getEditorXPosition() > width - 14) {
            scrollX -= (width - 14) - getEditorXPosition();
        }
        double editorY = 7 + this.editorY * FontRenderers.getMono().getMarginHeight() - smoothScroll;
        if (editorY < 7) {
            scroll += editorY - 7;
        } else if (editorY > height - 37) {
            scroll -= (height - 37) - editorY;
        }
        mouseScrolled(0, 0, 0);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        String index = initial.get(editorY);
        StringBuilder sb = new StringBuilder(index);
        sb.insert(editorX, chr);
        if (chr == '\"') sb.insert(editorX + 1, "\"");
        initial.set(editorY, sb.toString());
        editorX++;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void renderInternal(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Renderer.R2D.renderRoundedQuad(stack, new Color(20, 20, 20, 200), 5, 5, width - 5, height - 30, 5, 20);
        Renderer.R2D.beginScissor(stack, 5, 5, width - 5, height - 30);
        double initY = 7 - smoothScroll;
        double initX = 7 - smoothScrollX;
        double x = initX;
        double y = initY;
        for (String s : initial) {
            if (!(y < 7 - FontRenderers.getMono().getMarginHeight() || y > height - 30))
                for (int i = 0; i < s.toCharArray().length; i++) { // only render when in bounds
                    char c = s.charAt(i);
                    int color = getColor(s, i, c);
                    double cw = FontRenderers.getMono().getStringWidth(c + "");
                    if (x > 5 - cw && x < width - 5) {
                        FontRenderers.getMono().drawString(stack, c + "", x, y, color);
                    }
                    x += cw;
                }
            x = initX;
            y += FontRenderers.getMono().getMarginHeight();
            inString = false;
        }
        editorY = MathHelper.clamp(editorY, 0, initial.size() - 1);
        String index = initial.get(editorY);
        editorX = MathHelper.clamp(editorX, 0, index.length());
        String before = index.substring(0, editorX);
        double cx = FontRenderers.getMono().getStringWidth(before) + initX + 0.5;
        double cy = FontRenderers.getMono().getMarginHeight() * editorY + initY;
        Renderer.R2D.endScissor();
        Renderer.R2D.renderQuad(stack, Color.WHITE, cx, cy, cx + 1, cy + FontRenderers.getMono().getMarginHeight());
        super.renderInternal(stack, mouseX, mouseY, delta);
    }
}
