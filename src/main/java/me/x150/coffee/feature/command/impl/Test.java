package me.x150.coffee.feature.command.impl;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.command.Command;
import me.x150.coffee.feature.gui.screen.NbtEditorScreen;
import me.x150.coffee.helper.util.Utils;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(5, () -> {
            CoffeeClientMain.client.setScreen(new NbtEditorScreen(CoffeeClientMain.client.player.getInventory().getMainHandStack()));
        });
//        Notification.create(10000,"bruh", Notification.Type.INFO, "REALFDHJKFHDSJKFHSJKDFH KJDSHFJKDSHFKJDSHFJKDSaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaH FKJSDHJKFK HSDKJFHDSJKFHJKSD HJKFHDSJKF HDSJKFHSDJK HJKSDHF KSDHF KJDSHFKJSDH FJKSDHFJKS");
//        Notification.create(10000,"bruh", Notification.Type.WARNING, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.SUCCESS, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.ERROR, "REAL");

    }
}
