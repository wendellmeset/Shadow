package me.x150.coffee.feature.command.impl;

import me.x150.coffee.CoffeeClientMain;
import me.x150.coffee.feature.command.Command;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.Text;

import java.net.InetSocketAddress;

public class Test extends Command {
    public Test() {
        super("Test", "REAL", "test");
    }

    @Override
    public void onExecute(String[] args) {
        InetSocketAddress addr = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        ClientConnection conn = ClientConnection.connect(addr, CoffeeClientMain.client.options.shouldUseNativeTransport());
        conn.setPacketListener(new ClientLoginPacketListener() {
            @Override
            public void onHello(LoginHelloS2CPacket packet) {
                conn.disconnect(Text.of("your mother"));
            }

            @Override
            public void onSuccess(LoginSuccessS2CPacket packet) {

            }

            @Override
            public void onDisconnect(LoginDisconnectS2CPacket packet) {

            }

            @Override
            public void onCompression(LoginCompressionS2CPacket packet) {

            }

            @Override
            public void onQueryRequest(LoginQueryRequestS2CPacket packet) {

            }

            @Override
            public void onDisconnected(Text reason) {

            }

            @Override
            public ClientConnection getConnection() {
                return null;
            }
        });
        conn.send(new HandshakeC2SPacket(addr.getHostName(), addr.getPort(), NetworkState.LOGIN));
        conn.send(new LoginHelloC2SPacket(CoffeeClientMain.client.getSession().getProfile()));
//        conn.disconnect(Text.of("cumfart"));
    }
}
