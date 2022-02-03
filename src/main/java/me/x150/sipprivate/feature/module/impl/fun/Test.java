package me.x150.sipprivate.feature.module.impl.fun;

import io.netty.buffer.Unpooled;
import me.x150.sipprivate.feature.module.Module;
import me.x150.sipprivate.feature.module.ModuleType;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

public class Test extends Module {

    public Test() {
        super("Test", "Testing stuff with the client, can be ignored", ModuleType.FUN);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) return;
            PacketEvent pe = (PacketEvent) event;
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            pe.getPacket().write(buffer);
            System.out.println(pe.getPacket().getClass().getSimpleName() + " - " + new String(buffer.getWrittenBytes()).replaceAll("\\p{C}", "."));
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
}
