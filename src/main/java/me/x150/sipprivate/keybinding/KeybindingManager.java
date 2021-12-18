package me.x150.sipprivate.keybinding;

import me.x150.sipprivate.module.Module;
import me.x150.sipprivate.module.ModuleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * The keybind manager
 */
public class KeybindingManager {

    /**
     * List of all the modules with their respective keybinds
     */
    public static final Map<Module, Keybind> keybindMap = new HashMap<>();

    /**
     * Init the keybinding manager
     */
    public static void init() {
        for (Module module : ModuleManager.instance().getModules()) {
            keybindMap.put(module, new Keybind(module.keybind.getValue()));
        }
    }

    /**
     * Update a single keybind via keyboard event
     *
     * @param kc     The key which was changed
     * @param action The action performed (0 = release, 1 = pressed, 2 = repeat pressed when holding)
     */
    public static void updateSingle(int kc, int action) {
        if (kc == -1) {
            return; // JESSE WE FUCKED UP
        }
        if (action == 1) { // key pressed
            for (Module o : keybindMap.keySet().toArray(new Module[0])) {
                Keybind kb = keybindMap.get(o);
                if (kb.keycode() == kc) {
                    o.toggle();
                }
            }
        }
    }

    /**
     * Reloads the keybind manager
     */
    public static void reload() {
        keybindMap.clear();
        init();
    }

}
