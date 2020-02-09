package org.lordsofchaos.network;

import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ConnectionListener extends Thread
{
    private DatagramSocket socket;
    private static byte[] buffer = new byte[256];
    
    @SneakyThrows
    public ConnectionListener(int port) {
        socket = new DatagramSocket(port);
    }
    
    @SneakyThrows
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            System.out.printf("Connection request received from %s on port %d\n", address, port);
            packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            GameServer.addConnection(address, port);
        }
    }
}
