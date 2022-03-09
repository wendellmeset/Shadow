package net.shadow.client.feature.gui.clickgui.theme;

import net.shadow.client.feature.gui.clickgui.theme.impl.Custom;
import net.shadow.client.feature.gui.clickgui.theme.impl.SipoverV1;
import net.shadow.client.feature.module.ModuleRegistry;

public class ThemeManager {
    static net.shadow.client.feature.module.impl.render.Theme t = ModuleRegistry.getByClass(net.shadow.client.feature.module.impl.render.Theme.class);
    static Theme custom = new Custom();
    static Theme sippy = new SipoverV1();

    public static Theme getMainTheme() {
        return t.isEnabled() ? custom : sippy;
    }
}
