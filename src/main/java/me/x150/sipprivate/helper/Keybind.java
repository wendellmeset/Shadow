package me.x150.sipprivate.helper;

import me.x150.sipprivate.CoffeeClientMain;
import net.minecraft.client.util.InputUtil;

public class Keybind {
    final int keycode;

    public Keybind(int keycode) {
        this.keycode = keycode;
    }

    public boolean isPressed() {
        if (keycode < 0) return false;
        boolean isActuallyPressed = InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), keycode);
        return CoffeeClientMain.client.currentScreen == null && isActuallyPressed;
    }
}
