package me.x150.coffee.helper.font.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.io.InputStream;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class GlyphPageFontRenderer {

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for drop shadows.
     */
    private final int[] colorCode = new int[32];
    private final GlyphPage regularGlyphPage;
    private final GlyphPage boldGlyphPage;
    private final GlyphPage italicGlyphPage;
    private final GlyphPage boldItalicGlyphPage;
    public int size = -1;
    /**
     * Current X coordinate at which to draw the next character.
     */
    private float posX;
    /**
     * Current Y coordinate at which to draw the next character.
     */
    private float posY;
    /**
     * Set if the "l" style (bold) is active in currently rendering string
     */
    private boolean boldStyle;
    /**
     * Set if the "o" style (italic) is active in currently rendering string
     */
    private boolean italicStyle;
    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;
    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
    private boolean strikethroughStyle;

    public GlyphPageFontRenderer(GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage, GlyphPage boldItalicGlyphPage) {
        this.regularGlyphPage = regularGlyphPage;
        this.boldGlyphPage = boldGlyphPage;
        this.italicGlyphPage = italicGlyphPage;
        this.boldItalicGlyphPage = boldItalicGlyphPage;

        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    public static GlyphPageFontRenderer create(String fontName, int size, boolean bold, boolean italic, boolean boldItalic) {
        char[] chars = {0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f, 0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c, 0x7d, 0x7e, 0xa2, 0xa3, 0xa7, 0xa9, 0xb0, 0xb2, 0xb3, 0xb4, 0xb7, 0xb9, 0xbb, 0xbc, 0xbd, 0xbe, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc};
//        char[] chars = new char[256];
//        for (int i = 0; i < chars.length; i++) {
//            chars[i] = (char) i;
//        }

        GlyphPage regularPage;

        regularPage = new GlyphPage(new Font(fontName, Font.PLAIN, size), true, true);

        regularPage.generateGlyphPage(chars);
        regularPage.setupTexture();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        if (bold) {
            boldPage = new GlyphPage(new Font(fontName, Font.BOLD, size), true, true);

            boldPage.generateGlyphPage(chars);
            boldPage.setupTexture();
        }

        if (italic) {
            italicPage = new GlyphPage(new Font(fontName, Font.ITALIC, size), true, true);

            italicPage.generateGlyphPage(chars);
            italicPage.setupTexture();
        }

        if (boldItalic) {
            boldItalicPage = new GlyphPage(new Font(fontName, Font.BOLD | Font.ITALIC, size), true, true);

            boldItalicPage.generateGlyphPage(chars);
            boldItalicPage.setupTexture();
        }

        GlyphPageFontRenderer gpfr = new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
        gpfr.size = size;
        return gpfr;
    }

    public static GlyphPageFontRenderer createFromStream(InputStream inp, int size, boolean bold, boolean italic, boolean boldItalic) {
        char[] chars = new char[256];

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) i;
        }

        Font font = null;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, inp).deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlyphPage regularPage;

        regularPage = new GlyphPage(font, true, true);
        regularPage.generateGlyphPage(chars);
        regularPage.setupTexture();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        try {
            if (bold) {
                boldPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, inp).deriveFont(Font.BOLD, size), true, true);

                boldPage.generateGlyphPage(chars);
                boldPage.setupTexture();
            }

            if (italic) {
                italicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, inp).deriveFont(Font.ITALIC, size), true, true);

                italicPage.generateGlyphPage(chars);
                italicPage.setupTexture();
            }

            if (boldItalic) {
                boldItalicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, inp).deriveFont(Font.BOLD | Font.ITALIC, size), true, true);

                boldItalicPage.generateGlyphPage(chars);
                boldItalicPage.setupTexture();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
    }

    public static GlyphPageFontRenderer createFromID(String id, int size, boolean bold, boolean italic, boolean boldItalic) {
        char[] chars = new char[256];

        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) i;
        }

        Font font = null;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getClassLoader().getResourceAsStream(id))).deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlyphPage regularPage;

        regularPage = new GlyphPage(font, true, true);
        regularPage.generateGlyphPage(chars);
        regularPage.setupTexture();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        try {
            if (bold) {
                boldPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getClassLoader().getResourceAsStream(id)))
                        .deriveFont(Font.BOLD, size), true, true);

                boldPage.generateGlyphPage(chars);
                boldPage.setupTexture();
            }

            if (italic) {
                italicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getClassLoader().getResourceAsStream(id)))
                        .deriveFont(Font.ITALIC, size), true, true);

                italicPage.generateGlyphPage(chars);
                italicPage.setupTexture();
            }

            if (boldItalic) {
                boldItalicPage = new GlyphPage(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(GlyphPageFontRenderer.class.getClassLoader().getResourceAsStream(id)))
                        .deriveFont(Font.BOLD | Font.ITALIC, size), true, true);

                boldItalicPage.generateGlyphPage(chars);
                boldItalicPage.setupTexture();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        GlyphPageFontRenderer gp = new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
        gp.size = size;
        return gp;
    }

    public char[] getRegisteredCharacters() {
        Character[] shit1 = this.regularGlyphPage.glyphCharacterMap.keySet().toArray(new Character[0]);
        char[] shit = new char[shit1.length];
        for (int i = 0; i < shit1.length; i++) {
            shit[i] = shit1[i];
        }
        return shit;
    }

    public int drawString(MatrixStack matrices, String text, float x, float y, int color) {
        return drawString(matrices, text, x, y, color, false);
    }

    public int drawString(MatrixStack matrices, String text, double x, double y, int color) {
        return drawString(matrices, text, (float) x, (float) y, color, false);
    }

    public int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        return drawString(matrices, text, (float) x - getStringWidth(text) / 2f, (float) y, color, false);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(MatrixStack matrices, String text, float x, float y, int color, boolean dropShadow) {
        this.resetStyles();
        int i;

        if (dropShadow) {
            i = this.renderString(matrices, text, x + .5F, y + .5F, color, true);
            i = Math.max(i, this.renderString(matrices, text, x, y, color, false));
        } else {
            i = this.renderString(matrices, text, x, y, color, false);
        }

        return i;
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString(MatrixStack matrices, String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        } else {

            if ((color & 0xfc000000) == 0) {
                color |= 0xff000000;
            }

            if (dropShadow) {
                color = 0xFF323232;
            }
            this.posX = x * 2.0f;
            this.posY = y * 2.0f;
            this.renderStringAtPos(matrices, text, dropShadow, color);
            return (int) (this.posX / 4.0f);
        }
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos(MatrixStack matrices, String text, boolean shadow, int color) {
        GlyphPage glyphPage = getCurrentGlyphPage();
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        matrices.push();

        matrices.scale(0.5F, 0.5F, 0.5F);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableTexture();

        glyphPage.bindTexture();

        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);

            if (c0 == '§' && i + 1 < text.length()) {
                int i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(i + 1));

                if (i1 < 16) {
                    resetStyles();

                    if (i1 < 0) {
                        i1 = 15;
                    }

                    if (shadow) {
                        i1 += 16;
                    }

                    int j1 = this.colorCode[i1];

                    r = (float) (j1 >> 16 & 255) / 255.0F;
                    g = (float) (j1 >> 8 & 255) / 255.0F;
                    b = (float) (j1 & 255) / 255.0F;
                } else if (i1 == 17) {
                    this.boldStyle = true;
                } else if (i1 == 18) {
                    this.strikethroughStyle = true;
                } else if (i1 == 19) {
                    this.underlineStyle = true;
                } else if (i1 == 20) {
                    this.italicStyle = true;
                } else if (i1 == 21) {
                    resetStyles();
                    alpha = (float) (color >> 24 & 255) / 255.0F;
                    r = (float) (color >> 16 & 255) / 255.0F;
                    g = (float) (color >> 8 & 255) / 255.0F;
                    b = (float) (color & 255) / 255.0F;
                }

                ++i;
            } else {
                float f = glyphPage.drawChar(matrices, c0, posX, posY, r, b, g, alpha);

                drawUnderlineStrikethrough(f, glyphPage);
                this.posX += f;
            }
        }

        //glyphPage.unbindTexture();
        matrices.pop();

    }

    private void drawUnderlineStrikethrough(float f, GlyphPage glyphPage) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        GlStateManager._disableTexture();
        if (this.strikethroughStyle) {
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            bufferBuilder.vertex(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0D).next();
            bufferBuilder.vertex(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0D).next();
            bufferBuilder.vertex(this.posX + f, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0D).next();
            bufferBuilder.vertex(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0D).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            GlStateManager._enableTexture();
        }

        if (this.underlineStyle) {
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            bufferBuilder.vertex(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight(), 0.0D).next();
            bufferBuilder.vertex(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight(), 0.0D).next();
            bufferBuilder.vertex(this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0D).next();
            bufferBuilder.vertex(this.posX + (float) l, this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0D).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            GlStateManager._enableTexture();
        }

        //        this.posX += f;
    }

    private GlyphPage getCurrentGlyphPage() {
        if (boldStyle && italicStyle) {
            return boldItalicGlyphPage;
        } else if (boldStyle) {
            return boldGlyphPage;
        } else if (italicStyle) {
            return italicGlyphPage;
        } else {
            return regularGlyphPage;
        }
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of string rendering
     */
    private void resetStyles() {
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    public float getHeight(String text) {
        return regularGlyphPage.getFontHeight(text) / 2f;
    }

    public float getFontHeight() {
        return regularGlyphPage.getMaxFontHeight() / 2f;
    }

    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;

        GlyphPage currentPage;

        int size = text.length();

        boolean on = false;

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);

            if (character == '§') {
                on = true;
            } else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
                on = false;
            } else {
                on = false;
                character = text.charAt(i);

                currentPage = getCurrentGlyphPage();

                width += currentPage.getWidth(character) - 8;
                //i++;
            }
        }

        return width / 2;
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, double width) {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String text, double maxWidth, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();

        boolean on = false;

        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        int width = 0;

        GlyphPage currentPage;

        for (int i = j; i >= 0 && i < text.length(); i += k) {
            char character = text.charAt(i);

            if (character == '§') {
                on = true;
            } else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
                on = false;
            } else {
                on = false;
            }

            if (reverse) {
                stringbuilder.insert(0, character);
            } else {
                stringbuilder.append(character);
            }
            String currentText = stringbuilder.toString();
            if (getStringWidth(currentText) >= maxWidth) {
                break;
            }
        }

        return stringbuilder.toString();
    }
}

