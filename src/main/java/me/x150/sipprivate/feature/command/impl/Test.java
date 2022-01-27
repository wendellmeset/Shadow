package me.x150.sipprivate.feature.command.impl;

import me.x150.sipprivate.feature.command.Command;
import me.x150.sipprivate.feature.gui.notifications.Notification;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
//        Notification.create(10000,"bruh", Notification.Type.INFO, "REALFDHJKFHDSJKFHSJKDFH KJDSHFJKDSHFKJDSHFJKDSaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaH FKJSDHJKFK HSDKJFHDSJKFHJKSD HJKFHDSJKF HDSJKFHSDJK HJKSDHF KSDHF KJDSHFKJSDH FJKSDHFJKS");
//        Notification.create(10000,"bruh", Notification.Type.WARNING, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.SUCCESS, "REAL");
//        Notification.create(10000,"bruh", Notification.Type.ERROR, "REAL");

    }
}
