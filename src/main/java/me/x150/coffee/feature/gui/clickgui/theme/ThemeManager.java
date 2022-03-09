package me.x150.coffee.feature.gui.clickgui.theme;

import me.x150.coffee.feature.gui.clickgui.theme.impl.Custom;
import me.x150.coffee.feature.gui.clickgui.theme.impl.SipoverV1;
import me.x150.coffee.feature.module.ModuleRegistry;

public class ThemeManager {
    static me.x150.coffee.feature.module.impl.render.Theme t = ModuleRegistry.getByClass(me.x150.coffee.feature.module.impl.render.Theme.class);
    static Theme custom = new Custom();
    static Theme sippy = new SipoverV1();
    public static Theme getMainTheme() {
        return t.isEnabled()?custom:sippy;
    }
}
