package me.x150.sipprivate.feature.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import me.x150.sipprivate.helper.render.Renderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL40C;

import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RoundTextFieldWidget {
    private final FontAdapter textRenderer;
    private final Predicate<String> textPredicate;
    private final String suggestion;
    double x, y, width, height;
    boolean focussed = false;
    boolean visible = true;
    private String text;
    private int maxLength;
    private int focusedTicks;
    private boolean drawsBackground;
    private boolean editable;
    private boolean selecting;
    private int firstCharacterIndex;
    private int selectionStart;
    private int selectionEnd;
    @Nullable
    private Consumer<String> changedListener;

    public RoundTextFieldWidget(FontAdapter textRenderer, double x, double y, double width, double height, String suggestion) {
        this.text = "";
        this.maxLength = 1028;
        this.drawsBackground = true;
        this.editable = true;
        this.textPredicate = Objects::nonNull;
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.suggestion = suggestion;
    }

    public void setChangedListener(@Nullable Consumer<String> changedListener) {
        this.changedListener = changedListener;
    }

    public void tick() {
        ++this.focusedTicks;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        if (this.textPredicate.test(text)) {
            if (text.length() > this.maxLength) {
                this.text = text.substring(0, this.maxLength);
            } else {
                this.text = text;
            }

            this.setCursorToEnd();
            this.setSelectionEnd(this.selectionStart);
            this.onChanged(text);
        }
    }

    public String getSelectedText() {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void write(String text) {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        int k = this.maxLength - this.text.length() - (i - j);
        String string = SharedConstants.stripInvalidChars(text);
        int l = string.length();
        if (k < l) {
            string = string.substring(0, k);
            l = k;
        }

        String string2 = (new StringBuilder(this.text)).replace(i, j, string).toString();
        if (this.textPredicate.test(string2)) {
            this.text = string2;
            this.setSelectionStart(i + l);
            this.setSelectionEnd(this.selectionStart);
            this.onChanged(this.text);
        }
    }

    private void onChanged(String newText) {
        if (this.changedListener != null) {
            this.changedListener.accept(newText);
        }

    }

    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }

    }

    public void eraseWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
            }
        }
    }

    public void eraseCharacters(int characterOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                int i = this.getCursorPosWithOffset(characterOffset);
                int j = Math.min(i, this.selectionStart);
                int k = Math.max(i, this.selectionStart);
                if (j != k) {
                    String string = (new StringBuilder(this.text)).delete(j, k).toString();
                    if (this.textPredicate.test(string)) {
                        this.text = string;
                        this.setCursor(j);
                    }
                }
            }
        }
    }

    public int getWordSkipPosition(int wordOffset) {
        return this.getWordSkipPosition(wordOffset, this.getCursor());
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition) {
        return this.getWordSkipPosition(wordOffset, cursorPosition, true);
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
        int i = cursorPosition;
        boolean bl = wordOffset < 0;
        int j = Math.abs(wordOffset);

        for (int k = 0; k < j; ++k) {
            if (!bl) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int offset) {
        this.setCursor(this.getCursorPosWithOffset(offset));
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(this.text, this.selectionStart, offset);
    }

    public void setSelectionStart(int cursor) {
        this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    public boolean keyPressed(int keyCode) {
        if (!this.isActive()) {
            return false;
        } else {
            this.selecting = Screen.hasShiftDown();
            if (Screen.isSelectAll(keyCode)) {
                this.setCursorToEnd();
                this.setSelectionEnd(0);
                return true;
            } else if (Screen.isCopy(keyCode)) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                return true;
            } else if (Screen.isPaste(keyCode)) {
                if (this.editable) {
                    this.write(MinecraftClient.getInstance().keyboard.getClipboard());
                }

                return true;
            } else if (Screen.isCut(keyCode)) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                if (this.editable) {
                    this.write("");
                }

                return true;
            } else {
                switch (keyCode) {
                    case 259:
                        if (this.editable) {
                            this.selecting = false;
                            this.erase(-1);
                            this.selecting = Screen.hasShiftDown();
                        }

                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        return false;
                    case 261:
                        if (this.editable) {
                            this.selecting = false;
                            this.erase(1);
                            this.selecting = Screen.hasShiftDown();
                        }

                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 268:
                        this.setCursorToStart();
                        return true;
                    case 269:
                        this.setCursorToEnd();
                        return true;
                }
            }
        }
    }

    public boolean isActive() {
        return this.isVisible() && this.focussed && this.isEditable();
    }

    public void charTyped(char chr) {
        if (this.isActive() && SharedConstants.isValidChar(chr)) {
            if (this.editable) {
                this.write(Character.toString(chr));
            }

        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible()) {
            boolean bl = mouseX >= this.x && mouseX < (this.x + this.width) && mouseY >= this.y && mouseY < (this.y + this.height);

            if (bl && button == 0) {
                this.focussed = true;
                double i = MathHelper.floor(mouseX) - this.x;
                if (this.drawsBackground) {
                    i -= 4;
                }

                String string = this.textRenderer.trimStringToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
                this.setCursor(this.textRenderer.trimStringToWidth(string, i).length() + this.firstCharacterIndex);
            } else {
                this.focussed = false;
            }
        }
    }

    public void render(MatrixStack matrices) {
        if (this.isVisible()) {
            RenderSystem.enableBlend();
            RenderSystem.colorMask(false, false, false, true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            // use background as a mask for rendering :brain:
            Renderer.R2D.renderRoundedQuadInternal(matrices.peek().getPositionMatrix(), 1, 1, 1, 1, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 10, 10);
            RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
            int cursorIndexRelative = this.selectionStart - this.firstCharacterIndex;
            int selEndRelative = this.selectionEnd - this.firstCharacterIndex;
            String renderString = this.textRenderer.trimStringToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
            boolean cursorIndexInBounds = cursorIndexRelative >= 0 && cursorIndexRelative <= renderString.length();
            boolean bl2 = focussed && this.focusedTicks / 6 % 2 == 0 && cursorIndexInBounds;
            double renderX = this.drawsBackground ? this.x + 4 : this.x;
            double renderY = this.drawsBackground ? this.y + (this.height - textRenderer.getMarginHeight()) / 2 : this.y;
            double renderX2 = renderX;
            if (selEndRelative > renderString.length()) {
                selEndRelative = renderString.length();
            }

            if (!renderString.isEmpty()) {
                String string3 = cursorIndexInBounds ? renderString.substring(0, cursorIndexRelative) : renderString;
                double wid = this.textRenderer.getStringWidth(string3);
                //                if (wid > width) this.firstCharacterIndex++;
                this.textRenderer.drawString(matrices, renderString, (float) renderX, (float) renderY, 0, false);
                renderX2 += wid + 1;
            } else {
                this.textRenderer.drawString(matrices, suggestion, (float) renderX, (float) renderY, 0x666666, false);
            }

            boolean stringHasContent = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
            double cursorRenderPos = renderX2;
            if (!cursorIndexInBounds) {
                cursorRenderPos = cursorIndexRelative > 0 ? renderX + this.width : renderX;
            } else if (stringHasContent) {
                cursorRenderPos = renderX2;
                //                --renderX2;
            }

            double cursorFromY;
            double cursorToX;
            double cursorToY;
            if (bl2) {
                cursorFromY = renderY;
                cursorToX = cursorRenderPos + .5;

                Objects.requireNonNull(this.textRenderer);
                Renderer.R2D.renderQuad(matrices, Color.BLACK, cursorRenderPos, cursorFromY, cursorToX, cursorFromY + textRenderer.getMarginHeight());
            }

            if (selEndRelative != cursorIndexRelative) {
                double beginToSelEndWidX = renderX + this.textRenderer.getStringWidth(renderString.substring(0, selEndRelative));
                cursorFromY = renderY;
                cursorToX = beginToSelEndWidX;
                cursorToY = renderY;
                this.drawSelectionHighlight(cursorRenderPos, cursorFromY, cursorToX, cursorToY + textRenderer.getMarginHeight());
            }
            RenderSystem.defaultBlendFunc();
        }
    }

    private void drawSelectionHighlight(double x1, double y1, double x2, double y2) {
        double i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        if (x2 > this.x + this.width) {
            x2 = this.x + this.width;
        }

        if (x1 > this.x + this.width) {
            x1 = this.x + this.width;
        }

        Tessellator inst = Tessellator.getInstance();
        BufferBuilder bufferBuilder = inst.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(x1, y2, 0.0D).next();
        bufferBuilder.vertex(x2, y2, 0.0D).next();
        bufferBuilder.vertex(x2, y1, 0.0D).next();
        bufferBuilder.vertex(x1, y1, 0.0D).next();
        inst.draw();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    public void setCursor(int cursor) {
        this.setSelectionStart(cursor);
        if (!this.selecting) {
            this.setSelectionEnd(this.selectionStart);
        }

        this.onChanged(this.text);
    }

    private boolean drawsBackground() {
        return this.drawsBackground;
    }

    public void setDrawsBackground(boolean drawsBackground) {
        this.drawsBackground = drawsBackground;
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public double getInnerWidth() {
        return this.drawsBackground() ? this.width - 8 : this.width;
    }

    public void setSelectionEnd(int index) {
        int i = this.text.length();
        this.selectionEnd = MathHelper.clamp(index, 0, i);
        if (this.textRenderer != null) {
            if (this.firstCharacterIndex > i) {
                this.firstCharacterIndex = i;
            }

            double j = this.getInnerWidth();
            String string = this.textRenderer.trimStringToWidth(this.text.substring(this.firstCharacterIndex), j);
            int k = string.length() + this.firstCharacterIndex;
            if (this.selectionEnd == this.firstCharacterIndex) {
                this.firstCharacterIndex -= this.textRenderer.trimStringToWidth(this.text, j, true).length();
            }

            if (this.selectionEnd > k) {
                this.firstCharacterIndex += this.selectionEnd - k;
            } else if (this.selectionEnd <= this.firstCharacterIndex) {
                this.firstCharacterIndex -= this.firstCharacterIndex - this.selectionEnd;
            }

            this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, i);
        }

    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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
}
