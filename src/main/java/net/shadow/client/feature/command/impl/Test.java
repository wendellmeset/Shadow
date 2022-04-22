/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.command.impl;

import net.minecraft.item.ItemStack;
import net.shadow.client.WhatTheFuck;
import net.shadow.client.feature.command.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        WhatTheFuck.loadTags();
        try {
            File out = new File("nbt.shit");
            FileOutputStream fos = new FileOutputStream(out);
            for (Field declaredField : WhatTheFuck.class.getDeclaredFields()) {
                if (declaredField.getType().equals(List.class)) {
                    declaredField.setAccessible(true);
                    List<ItemStack> l = (List<ItemStack>) declaredField.get(null);
                    for (ItemStack stack : l) {
                        String fName = stack.getItem().toString();
                        String nbt = Base64.getEncoder().encodeToString(stack.getOrCreateNbt().toString().getBytes(StandardCharsets.UTF_8));
                        String p = declaredField.getName() + ";" + fName + ";" + nbt;
                        fos.write((p + "\n").getBytes(StandardCharsets.UTF_8));
                    }

                }
            }
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
