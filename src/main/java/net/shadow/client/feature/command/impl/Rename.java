

package net.shadow.client.feature.command.impl;

import net.minecraft.text.Text;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.command.Command;

import java.util.Objects;

public class Rename extends Command {

    public Rename() {
        super("Rename", "Renames an item (requires creative)", "rename", "rn", "name");
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(new item name)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 0) {
            error("I need a new name dude");
            error("example: rename &c&lthe &afunny");
            return;
        }
        if (Objects.requireNonNull(ShadowMain.client.player).getInventory().getMainHandStack().isEmpty()) {
            error("idk if you're holding anything");
            return;
        }
        ShadowMain.client.player.getInventory().getMainHandStack().setCustomName(Text.of("§r" + String.join(" ", args).replaceAll("&", "§")));
    }
}
