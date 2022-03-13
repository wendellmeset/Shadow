/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper.font;

import net.shadow.client.helper.font.adapter.FontAdapter;
import net.shadow.client.helper.font.adapter.impl.ClientFontRenderer;
import net.shadow.client.helper.font.render.GlyphPageFontRenderer;

import java.util.ArrayList;
import java.util.List;

public class FontRenderers {
    private static final List<ClientFontRenderer> fontRenderers = new ArrayList<>();
    private static FontAdapter normal;
    private static FontAdapter mono;

    public static FontAdapter getRenderer() {
        return normal;
    }

    public static void setRenderer(FontAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static FontAdapter getMono() {
        if (mono == null) {
            mono = new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Mono.ttf", 17, false, false, false));
        }
        return mono;
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
