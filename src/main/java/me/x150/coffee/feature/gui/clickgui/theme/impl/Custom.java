package me.x150.coffee.feature.gui.clickgui.theme.impl;

import me.x150.coffee.feature.gui.clickgui.theme.Theme;
import me.x150.coffee.feature.module.ModuleRegistry;

import java.awt.*;

public class Custom implements Theme {
    me.x150.coffee.feature.module.impl.render.Theme theme = ModuleRegistry.getByClass(me.x150.coffee.feature.module.impl.render.Theme.class);
    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public Color getAccent() {
        return theme.accent.getValue();
    }

    @Override
    public Color getHeader() {
        return theme.header.getValue();
    }

    @Override
    public Color getModule() {
        return theme.module.getValue();
    }

    @Override
    public Color getConfig() {
        return theme.configC.getValue();
    }

    @Override
    public Color getActive() {
        return theme.active.getValue();
    }

    @Override
    public Color getInactive() {
        return theme.inactive.getValue();
    }
}
