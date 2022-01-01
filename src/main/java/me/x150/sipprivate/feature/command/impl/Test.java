package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.feature.gui.screen.TestScreen;
import me.x150.sipprivate.helper.util.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(5, () -> {
            CoffeeClientMain.client.setScreen(new TestScreen());
        });
    }
}
