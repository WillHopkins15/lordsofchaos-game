package org.lordsofchaos.network;

import lombok.SneakyThrows;

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
    }
    
    @SneakyThrows
    public void sendEcho(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        System.out.printf("Sent %s to %s\n", new String(packet.getData(), 0, packet.getLength()), address);
        socket.send(packet);
    }
    
    @SneakyThrows
    public void makeConnection() {
        for (String item : HostManager.getHosts()) {
            address = InetAddress.getByName(item);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 3333);
            socket.send(packet);
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                System.out.printf("Host %s not available.\n", address);
                continue;
            }
            System.out.println("Server found!");
            System.out.println("Looking for opponent...");
            socket.receive(packet);
            port = packet.getPort();
            System.out.printf("Connected to %s on port %d\n", address, port);
            return;
        }
        System.out.println("No Servers Online.");
    }
    
    public void close() {
        socket.close();
    }
}
