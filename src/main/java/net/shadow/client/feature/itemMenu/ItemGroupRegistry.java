/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.itemMenu;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemGroupRegistry {
    public static ShadItemGroup EXPLOITS = new ShadItemGroup("Exploits", new ItemStack(Items.ARMOR_STAND));
    public static ShadItemGroup GRIEF = new ShadItemGroup("Grief", new ItemStack(Items.TNT));
    public static ShadItemGroup CRASH = new ShadItemGroup("Crash", new ItemStack(Items.FIRE_CHARGE));

    public static void addItem(ShadItemGroup group, ItemStack stack) {
        group.addItem(stack);
    }

    public static void addItem(ShadItemGroup group, Item item, String nbt) {
        group.addItem(item, nbt);
    }

    public static void init() {
        initExploits();
    }

    static void initExploits() {
        addItem(ItemGroupRegistry.CRASH, Items.ARMOR_STAND, "{EntityTag:{CustomName:\"\\\"Amogi\\\"\", CustomNameVisible: true}}");
    }
}
