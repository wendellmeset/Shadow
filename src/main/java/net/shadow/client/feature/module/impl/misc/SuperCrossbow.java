/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.client.util.math.MatrixStack;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;

public class SuperCrossbow extends Module {

    public static final String inbt = "{Enchantments:[{id:\"minecraft:quick_charge\",lvl:5s}],ChargedProjectiles:[{},{id:\"minecraft:arrow\",Count:1b},{}],Charged:1b}";
    private static ItemStack stack;
    ItemStack before = new ItemStack(Registry.ITEM.get(new Identifier("air")), 1);

    public SuperCrossbow() {
        super("SuperCrossbow", "shoot arrows really quickly (press middle mouse)", ModuleType.MISC);
    }

    @Override
    public void tick() {
        if (!getItemNameFromStack(client.player.getMainHandStack()).equals(getItemNameFromStack(stack))) {
            before = client.player.getMainHandStack();
        }
        if (client.options.pickItemKey.isPressed()) {
            client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + client.player.getInventory().selectedSlot, stack));
            client.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND));
            client.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.UP));
            client.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + client.player.getInventory().selectedSlot, before));
        }
    }

    @Override
    public void enable() {
        if (stack == null) {
            stack = new ItemStack(Registry.ITEM.get(new Identifier("crossbow")), 1);
            try {
                stack.setNbt(StringNbtReader.parse(inbt));
            } catch (Exception ignored) {

            }
        }
    }
    private String getItemNameFromStack(ItemStack hstack) {
        String hs = hstack.getItem().getTranslationKey();
        hs = hs.replace("minecraft.", "").replace("block.", "").replace("item.", "");
        return hs;
    }

    @Override
    public void disable() {
    }

    @Override
    public String getContext() {
        return null;
    }

    @Override
    public void onWorldRender(MatrixStack matrices) {

    }

    @Override
    public void onHudRender() {

    }
}
