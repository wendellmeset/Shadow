package net.shadow.client.helper;

import net.minecraft.client.util.InputUtil;
import net.shadow.client.CoffeeClientMain;

public record Keybind(int keycode) {

    public boolean isPressed() {
        if (keycode < 0) {
            return false;
        }
        boolean isActuallyPressed = InputUtil.isKeyPressed(CoffeeClientMain.client.getWindow().getHandle(), keycode);
        return CoffeeClientMain.client.currentScreen == null && isActuallyPressed;
    }
}
