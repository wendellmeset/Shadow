package me.x150.coffee.feature.items;

import me.x150.coffee.feature.items.impl.Nuke;

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
    }

    public List<Item> getItems() {
        return items;
    }
}
