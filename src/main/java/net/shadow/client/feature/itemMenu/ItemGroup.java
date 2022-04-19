/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.itemMenu;

import lombok.Getter;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.shadow.client.helper.util.Utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemGroup {
    @Getter
    String translationKey;
    @Getter
    ItemStack icon;
    @Getter
    List<ItemStack> items = new CopyOnWriteArrayList<>();
    public ItemGroup(String translationkey, ItemStack icon) {
        this.translationKey = translationkey;
        this.icon = icon;
        FabricItemGroupBuilder.create(new Identifier("shadow", translationkey)).icon(() -> icon).appendItems((itemStacks, itemGroup) -> {
            itemStacks.addAll(this.getItems());
        }).build();
    }
    public void addItem(ItemStack stack) {
        items.add(stack);
    }
    public void addItem(Item type, String nbt) {
        addItem(Utils.generateItemStackWithMeta(nbt, type));
    }
}
