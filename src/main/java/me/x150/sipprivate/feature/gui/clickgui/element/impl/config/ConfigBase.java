package me.x150.sipprivate.feature.gui.clickgui.element.impl.config;

import me.x150.sipprivate.feature.config.SettingBase;
import me.x150.sipprivate.feature.gui.clickgui.element.Element;

public abstract class ConfigBase<C extends SettingBase<?>> extends Element {
    final C configValue;

    public ConfigBase(double x, double y, double width, double height, C configValue) {
        super(x, y, width, height);
        this.configValue = configValue;
    }

    public C getConfigValue() {
        return configValue;
    }
}
