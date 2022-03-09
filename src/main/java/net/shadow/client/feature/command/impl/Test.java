package net.shadow.client.feature.command.impl;

import net.shadow.client.feature.command.Command;
import net.shadow.client.helper.nbt.NbtGroup;
import net.shadow.client.helper.nbt.NbtList;
import net.shadow.client.helper.nbt.NbtObject;
import net.shadow.client.helper.nbt.NbtProperty;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        NbtGroup ng = new NbtGroup(
                new NbtObject("EntityTag",
                        new NbtProperty("id", "minecraft:armor_stand"),
                        new NbtProperty("CustomNameVisible", true),
                        new NbtProperty("Invulnerable", true),
                        new NbtProperty("HasVisualFire", true),
                        new NbtProperty("Glowing", true),
                        new NbtProperty("ShowArms", false),
                        new NbtProperty("NoBasePlate", true),
                        new NbtProperty("PersistanceRequired", true),
                        new NbtList("Motion",
                                new NbtProperty(0d),
                                new NbtProperty(2d),
                                new NbtProperty(0d)
                        ),
                        new NbtProperty("CustomName", "{\"text\": \"REAL\"}")
                )
        );
        System.out.println(ng.toCompound().toString());
    }
}
