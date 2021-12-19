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
        return new Color(0xE2334756, true);
    }

    @Override
    public Color getModule() {
        return new Color(0xDF2C394B, true);
    }

    @Override
    public Color getConfig() {
        return new Color(0xE7082032, true);
    }

    @Override
    public Color getActive() {
        return new Color(0, 159, 227, 255);
    }

    @Override
    public Color getInactive() {
        return new Color(78, 94, 103, 255);
    }
}
