package me.x150.coffee.helper.font;

import me.x150.coffee.helper.font.adapter.FontAdapter;
import me.x150.coffee.helper.font.adapter.impl.ClientFontRenderer;
import me.x150.coffee.helper.font.render.GlyphPageFontRenderer;

import java.util.ArrayList;
import java.util.List;

public class FontRenderers {
    private static final List<ClientFontRenderer> fontRenderers = new ArrayList<>();
    private static FontAdapter normal;

    public static FontAdapter getRenderer() {
        return normal;
    }

    public static void setRenderer(FontAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static ClientFontRenderer getCustomSize(int size) {
        for (ClientFontRenderer fontRenderer : fontRenderers) {
            if (fontRenderer.getSize() == size) {
                return fontRenderer;
            }
        }
        ClientFontRenderer cfr = new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", size, false, false, false));
        fontRenderers.add(cfr);
        return cfr;
    }
}
