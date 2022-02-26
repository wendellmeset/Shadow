package me.x150.coffee.helper;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManagerThread extends Thread {
    public AtomicBoolean needRestart = new AtomicBoolean(false);
    ManagerSocket socket;

    void startSocket() throws Exception {
        socket = new ManagerSocket(() -> {
            System.out.println("Reconnecting");
            needRestart.set(true);
        });
        if (!socket.connectBlocking(2000, TimeUnit.SECONDS)) needRestart.set(true);
    }

    public ManagerSocket getSocket() {
        return socket;
    }

    public void clear() {
        try {
            socket.closeBlocking();
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            startSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
