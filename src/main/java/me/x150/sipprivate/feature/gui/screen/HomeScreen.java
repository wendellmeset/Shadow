package me.x150.sipprivate.feature.gui.screen;

import me.x150.sipprivate.CoffeeClientMain;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

public class HomeScreen extends Screen {
    public HomeScreen() {
        super(Text.of(""));
    }

    @Override protected void init() {
        CoffeeClientMain.client.setScreen(new TitleScreen());
        super.init();
    }
}
