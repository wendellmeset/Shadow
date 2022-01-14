/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.module.impl.combat;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.config.EnumSetting;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.feature.module.impl.movement.NoFall;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Criticals extends Module {

    final EnumSetting<Mode> mode = this.config.create(new EnumSetting.Builder<>(Mode.Packet)
            .name("Mode")
            .description("How to deal crits")
            .get());

    public Criticals() {
        super("Criticals", "Makes you deal a perfect 10/10 crit every time", ModuleType.COMBAT);
        Events.registerEventHandler(EventType.PACKET_SEND, event1 -> {
            PacketEvent event = (PacketEvent) event1;
            if (CoffeeClientMain.client.player == null || CoffeeClientMain.client.getNetworkHandler() == null) {
                return;
            }
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket && this.isEnabled()) {
                Vec3d ppos = CoffeeClientMain.client.player.getPos();
                ModuleRegistry.getByClass(NoFall.class).enabled = false; // disable nofall modifying packets when we send these
                switch (mode.getValue()) {
                    case Packet -> {
                        PlayerMoveC2SPacket.PositionAndOnGround p1 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 0.2, ppos.z, true);
                        PlayerMoveC2SPacket.PositionAndOnGround p2 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y, ppos.z, false);
                        PlayerMoveC2SPacket.PositionAndOnGround p3 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 0.000011, ppos.z, false);
                        PlayerMoveC2SPacket.PositionAndOnGround p4 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y, ppos.z, false);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p1);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p2);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p3);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p4);
                    }
                    case TpHop -> {
                        PlayerMoveC2SPacket.PositionAndOnGround p5 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 0.02, ppos.z, false);
                        PlayerMoveC2SPacket.PositionAndOnGround p6 = new PlayerMoveC2SPacket.PositionAndOnGround(ppos.x, ppos.y + 0.01, ppos.z, false);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p5);
                        CoffeeClientMain.client.getNetworkHandler().sendPacket(p6);
                    }
                }
                ModuleRegistry.getByClass(NoFall.class).enabled = true; // re-enable nofall
            }
        });

    }

    @Override
    public void tick() {

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

    //    final MultiValue mode = (MultiValue) this.config.create("Mode", "packet", "packet", "tphop").description("The mode");
    public enum Mode {
        Packet, TpHop
    }
}

