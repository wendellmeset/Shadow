package me.x150.coffee.feature.command.impl;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.command.Command;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.Text;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        String u = String.join(" ", args);
        ResourcePackSendS2CPacket rcsp = new ResourcePackSendS2CPacket(u, "0".repeat(40), false, Text.of("your momma"));
        CoffeeClientMain.client.getNetworkHandler().onResourcePackSend(rcsp);
//        Utils.TickManager.runInNTicks(5, () -> {
//            CoffeeClientMain.client.setScreen(new NbtEditorScreen(CoffeeClientMain.client.player.getInventory().getMainHandStack()));
//        });
//        Notification.create(10000,"bruh", Notification.Type.INFO, "REALFDHJKFHDSJKFHSJKDFH KJDSHFJKDSHFKJDSHFJKDSaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaH FKJSDHJKFK HSDKJFHDSJKFHJKSD HJKFHDSJKF HDSJKFHSDJK HJKSDHF KSDHF KJDSHFKJSDH FJKSDHFJKS");
//        Notification.create(10000,"bruh", Notification.Type.WARNING, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.SUCCESS, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.ERROR, "REAL");

    }
}
