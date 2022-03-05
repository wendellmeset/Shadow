package me.x150.coffee.helper;

import me.x150.coffee.CoffeeClientMain;
import net.minecraft.client.util.InputUtil;

public record Keybind(int keycode) {

    public boolean isPressed() {
        if (keycode < 0) {
            return false;
        }
        boolean isActuallyPressed = InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), keycode);
        return CoffeeClientMain.client.currentScreen == null && isActuallyPressed;
    }
}
