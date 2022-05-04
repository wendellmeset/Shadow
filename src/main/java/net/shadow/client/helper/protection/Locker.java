package net.shadow.client.helper.protection;

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.IPCUser;

public class Locker {

    public static void init() {
        boolean result = DiscordIPC.start(958479347390500874L, () -> {
            IPCUser user = DiscordIPC.getUser();
            //user.id
            DiscordIPC.stop();
        });
        if (!result) {
            System.out.println("Please have discord open while launching the client");
            System.exit(1);
        }
    }

}
