package me.x150.sipprivate.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The module manager
 */
public class ModuleManager {

    private static final ModuleManager instance = new ModuleManager();
    /**
     * A list of all registered modules
     */
    private final        List<Module>  modules  = new ArrayList<>();

    private ModuleManager() {
        addModules();
    }

    public static ModuleManager instance() {
        return instance;
    }

    /**
     * Adds all modules we want to register
     */
    public void addModules() {

    }

    /**
     * Registers a module
     *
     * @param module The module to register
     */
    private void addModule(Module module) {
        modules.add(module);
    }

    /**
     * Returns all modules
     *
     * @return The modules
     */
    @NotNull public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    /**
     * Returns the first module which has the name we specified, null if no module matches
     *
     * @param name The name we want to find
     * @return The module found, null otherwise
     */
    @Nullable public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }

        return null;
    }
}
