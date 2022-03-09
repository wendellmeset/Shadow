package me.x150.coffee.feature.module.impl.render;

import me.x150.coffee.feature.config.ColorSetting;
import me.x150.coffee.feature.module.Module;
import me.x150.coffee.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class Theme extends Module {
    public ColorSetting accent = this.config.create(new ColorSetting.Builder(new Color(0x3AD99D))
            .name("Accent")
            .description("The accent color")
            .get());
    public ColorSetting header = this.config.create(new ColorSetting.Builder(new Color(0xFF1D2525, true))
            .name("Header")
            .description("The header color")
            .get());
    public ColorSetting module = this.config.create(new ColorSetting.Builder(new Color(0xFF171E1F, true))
            .name("Module")
            .description("The module color")
            .get());
    public ColorSetting configC = this.config.create(new ColorSetting.Builder(new Color(0xFF111A1A, true))
            .name("Config")
            .description("The config section color")
            .get());
    public ColorSetting active = this.config.create(new ColorSetting.Builder(new Color(21, 157, 204, 255))
            .name("Active")
            .description("The active color")
            .get());
    public ColorSetting inactive = this.config.create(new ColorSetting.Builder( new Color(66, 66, 66, 255))
            .name("Inactive")
            .description("The inactive color")
            .get());
    public Theme() {
        super("CustomTheme", "Allows you to edit the client's appearance (enable to apply)", ModuleType.RENDER);

    }

    @Override
    public void tick() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
