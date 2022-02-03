package me.x150.sipprivate.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.x150.sipprivate.feature.module.ModuleRegistry;
import me.x150.sipprivate.feature.module.impl.misc.AntiPacketKick;
import me.x150.sipprivate.helper.event.EventType;
import me.x150.sipprivate.helper.event.Events;
import me.x150.sipprivate.helper.event.events.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void atomic_dispatchPacketGet(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (Events.fireEvent(EventType.PACKET_RECEIVE, new PacketEvent(packet))) {
            ci.cancel();
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    public void atomic_catchException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (ModuleRegistry.getByClass(AntiPacketKick.class).isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", cancellable = true, at = @At("HEAD"))
    public void atomic_dispatchPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (Events.fireEvent(EventType.PACKET_SEND, new PacketEvent(packet))) {
            ci.cancel();
        }
    }

}
