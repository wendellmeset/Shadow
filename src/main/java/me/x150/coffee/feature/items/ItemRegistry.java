package me.x150.coffee.feature.items;

import me.x150.coffee.feature.items.impl.Nuke;
import me.x150.coffee.feature.items.impl.Plague;

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
    }

    public List<Item> getItems() {
        return items;
    }
}
