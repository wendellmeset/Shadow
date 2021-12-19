package me.x150.sipprivate.feature.gui.clickgui.theme.impl;

import me.x150.sipprivate.feature.gui.clickgui.theme.Theme;

import java.awt.*;

public class SipoverV1 implements Theme {

    @Override
    public String getName() {
        return "Sipover V1";
    }

    @Override
    public Color getAccent() {
        return new Color(0x29FFB4);
    }

    @Override
    public Color getHeader() {
        return new Color(0xC31F1F1F, true);
    }

    @Override
    public Color getModule() {
        return new Color(0xD0131313, true);
    }

    @Override
    public Color getConfig() {
        return new Color(0xEF0E0E0E, true);
    }

    @Override
    public Color getActive() {
        return new Color(210, 75, 2, 255);
    }

    @Override
    public Color getInactive() {
        return new Color(42, 42, 42, 255);
    }
}
