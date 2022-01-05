package me.x150.sipprivate.mixin;

import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SliderWidget.class)
public interface ISliderWidgetAccessor {

    @Accessor("value")
    double getValue();
}
