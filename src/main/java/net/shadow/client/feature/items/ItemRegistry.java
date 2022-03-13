/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.items;

import net.shadow.client.feature.items.impl.Nuke;
import net.shadow.client.feature.items.impl.Plague;
import net.shadow.client.feature.items.impl.Poof;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final ItemRegistry instance = new ItemRegistry();
    final List<Item> items = new ArrayList<>();

    private ItemRegistry() {
        init();
    }

    void init() {
        items.clear();
        items.add(new Nuke());
        items.add(new Plague());
        items.add(new Poof());
    }

    public List<Item> getItems() {
        return items;
    }
}
