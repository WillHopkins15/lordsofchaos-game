package org.lordsofchaos.network;

import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class GameServer
{
    private static final int MAX_CONNECTIONS = 2;
    protected static final int SERV_PORT = 3333;
    protected static String[] connections = new String[MAX_CONNECTIONS];
    private static byte[] buffer = new byte[256];
    
    @SneakyThrows
    public static void main(String[] args) {
        HostManager.addHost(InetAddress.getLocalHost().getHostName());
        DatagramSocket socket = new DatagramSocket(SERV_PORT);
        
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting for a connection...");
            socket.receive(packet);
            
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            System.out.printf("Connection request received from %s on port %d\n", address, port);
            new GameThread(address, port).start();
            connections[i] = address.toString();
        }
        socket.close();
    }
}
