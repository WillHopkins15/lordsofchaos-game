package org.lordsofchaos.network;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class GameClient
{
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf = new byte[256];
    private int port;
    
    @SneakyThrows
    public GameClient() {
        socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        for (String item : HostManager.getHosts()) {
            address = InetAddress.getByName(item);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3333);
            socket.send(packet);
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                System.out.printf("Host %s not available", item);
                continue;
            }
            port = packet.getPort();
            System.out.printf("Connected to %s on port %d\n", address, port);
            break;
        }
    }
    
    @SneakyThrows
    public void sendEcho(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        System.out.printf("Sent %s to %s\n", new String(packet.getData(), 0, packet.getLength()), address);
        socket.send(packet);
    }
    
    public void close() {
        socket.close();
    }
}
