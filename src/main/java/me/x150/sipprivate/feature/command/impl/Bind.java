package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.feature.gui.screen.BindScreen;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.helper.util.Utils;

import java.util.stream.Collectors;

public class Bind extends Command {
    public Bind() {
        super("Bind", "Sets the keybind of a module", "bind");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(Module::getName).collect(Collectors.toList()).toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("Give me a module name");
            return;
        }
        String mn = args[0];
        Module module = ModuleRegistry.getByName(mn);
        if (module == null) {
            error("Module not found");
            return;
        }
        BindScreen bs = new BindScreen(module);
        Utils.TickManager.runInNTicks(5, () -> CoffeeClientMain.client.setScreen(bs));
    }
}
