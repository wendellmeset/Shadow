package me.x150.sipprivate.helper.font;

import me.x150.sipprivate.helper.font.adapter.FontAdapter;
import me.x150.sipprivate.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.sipprivate.helper.font.render.GlyphPageFontRenderer;

import java.util.ArrayList;
import java.util.List;

public class FontRenderers {
    private static final List<ClientFontRenderer> fontRenderers = new ArrayList<>();
    private static FontAdapter normal, title, mono, vanilla;

    public static FontAdapter getNormal() {
        return normal;
    }

    public static void setNormal(FontAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static ClientFontRenderer getCustomNormal(int size) {
        for (ClientFontRenderer fontRenderer : fontRenderers) {
            if (fontRenderer.getSize() == size) {
                return fontRenderer;
            }
        }
        ClientFontRenderer cfr = new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", size, false, false, false));
        fontRenderers.add(cfr);
        return cfr;
    }

    public static FontAdapter getMono() {
        return mono;
    }

    public static void setMono(FontAdapter mono) {
        FontRenderers.mono = mono;
    }

    public static FontAdapter getTitle() {
        return title;
    }

    public static void setTitle(FontAdapter title) {
        FontRenderers.title = title;
    }

    public static FontAdapter getVanilla() {
        return vanilla;
    }

    public static void setVanilla(FontAdapter vanilla) {
        FontRenderers.vanilla = vanilla;
    }
}
