package me.x150.sipprivate.feature.gui.clickgui.theme.impl;

import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;

import java.awt.Color;

public class SipoverV1 implements Theme {

    @Override public String getName() {
        return "Sipover V1";
    }

    @Override public Color getAccent() {
        return new Color(0x29FFB4);
    }

    @Override public Color getHeader() {
        return new Color(0xFF1D2525, true);
    }

    @Override public Color getModule() {
        return new Color(0xFF171E1F, true);
    }

    @Override public Color getConfig() {
        return new Color(0xFF111A1A, true);
    }

    @Override public Color getActive() {
        return new Color(222, 67, 0, 255);
    }

    @Override public Color getInactive() {
        return new Color(80, 80, 80, 255);
    }
}
