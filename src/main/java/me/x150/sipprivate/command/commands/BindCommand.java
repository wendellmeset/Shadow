package me.x150.sipprivate.command.commands;

import me.x150.sipprivate.command.Command;
import me.x150.sipprivate.keybinding.KeybindingManager;
import me.x150.sipprivate.module.Module;
import me.x150.sipprivate.module.ModuleManager;
import me.x150.sipprivate.util.ChatUtil;

public class BindCommand extends Command {

    public BindCommand() {
        super("BindCommand", "bind");
    }

    @Override public void runCommand(String[] args) {
        if (args.length < 2) {
            ChatUtil.send("I need the module and key to bind");
            return;
        }
        String mod = args[0];
        String key = args[1];
        if (key.length() != 1) {
            ChatUtil.send("One character as key allowed only");
            return;
        }

        int kc = key.toUpperCase().charAt(0);
        Module m = ModuleManager.instance().getModuleByName(mod);
        if (m == null) {
            ChatUtil.send("Cant find that module");
            return;
        }
        ChatUtil.send("BIND " + m.getName() + " -> " + kc + " (" + ((char) kc) + ")");
        //m.config.get("Keybind").setValue(kc);
        m.keybind.setValue(kc);
        KeybindingManager.reload();
    }
}
