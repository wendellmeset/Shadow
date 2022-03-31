/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.feature.module.impl.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.block.Block;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.shadow.client.ShadowMain;
import net.shadow.client.feature.config.ColorSetting;
import net.shadow.client.feature.module.Module;
import net.shadow.client.feature.module.ModuleType;
import net.shadow.client.helper.event.EventType;
import net.shadow.client.helper.event.Events;
import net.shadow.client.helper.event.events.PacketEvent;
import net.shadow.client.helper.render.Renderer;
import net.shadow.client.helper.util.Utils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;

public class Test extends Module {

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) return;
            PacketEvent pe = (PacketEvent) event;
            Packet<?> p = pe.getPacket();
            if (p instanceof PlayerMoveC2SPacket) return;
            System.out.println("-> "+p.getClass().getSimpleName());
            for (Field declaredField : p.getClass().getDeclaredFields()) {
                try {
                    declaredField.setAccessible(true);
                    Object val = declaredField.get(p);
                    System.out.println("    "+declaredField.getName()+": "+val.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void tick() {
//        int slot0 = Utils.Inventory.slotIndexToId(0);
//        ClickSlotC2SPacket p1 = new ClickSlotC2SPacket(client.player.currentScreenHandler.syncId,1,slot0,0,SlotActionType.PICKUP,client.player.getInventory().getStack(0), Int2ObjectMaps.singleton(slot0,new ItemStack(Items.AIR)));
//        ClickSlotC2SPacket p2 = new ClickSlotC2SPacket(client.player.currentScreenHandler.syncId,1,slot0,0,SlotActionType.PICKUP,new ItemStack(Items.AIR), Int2ObjectMaps.singleton(slot0,client.player.getInventory().getStack(0)));
//        client.getNetworkHandler().sendPacket(p1);
//        Int2ObjectMap<ItemStack> i2o = new Int2ObjectArrayMap<>();
//        for(int i = 9;i<28;i++) {
//            i2o.put(Utils.Inventory.slotIndexToId(i),new ItemStack(Items.ANDESITE,1));
//        }
//        ClickSlotC2SPacket p = new ClickSlotC2SPacket(client.player.currentScreenHandler.syncId,1, -999,6, SlotActionType.QUICK_CRAFT,client.player.getInventory().getStack(0),i2o);
//        client.getNetworkHandler().sendPacket(p);
//        client.getNetworkHandler().sendPacket(p2);
    }

    @Override
    public void enable() {

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
