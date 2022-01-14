package me.x150.sipprivate.feature.gui.clickgui.theme.impl;

import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;

import java.awt.*;

public class SipoverV1 implements Theme {

    static final Color accent = new Color(0x3AD99D);
    static final Color header = new Color(0xFF1D2525, true);
    static final Color module = new Color(0xFF171E1F, true);
    static final Color config = new Color(0xFF111A1A, true);
    static final Color active = new Color(21, 157, 204, 255);
    static final Color inactive = new Color(66, 66, 66, 255);

    @Override
    public String getName() {
        return "Sipover V1";
    }

    @Override
    public Color getAccent() {
        return accent;
    }

    @Override
    public Color getHeader() {
        return header;
    }

    @Override
    public Color getModule() {
        return module;
    }

    @Override
    public Color getConfig() {
        return config;
    }

    @Override
    public Color getActive() {
        return active;
    }

    @Override
    public Color getInactive() {
        return inactive;
    }
}
