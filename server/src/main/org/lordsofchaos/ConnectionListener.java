package org.lordsofchaos;

import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Thread for listening to new UDP connections on the specified port number. New connections
 * are added to a list in the {@link GameServer} class.
 *
 * @author Will Hopkins
 */
public class ConnectionListener extends Thread
{
    private static byte[] buffer = new byte[256];
    public boolean running = true;
    private DatagramSocket socket;
    
    /**
     * Creates a UDP DatagramSocket on the specified port, if the port is available.
     *
     * @param port Port number to open on
     */
    @SneakyThrows
    public ConnectionListener(int port) {
        socket = new DatagramSocket(port);
    }
    
    @SneakyThrows
    public void run() {
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (SocketException ignored) {
                return;
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            System.out.printf("Connection request received from %s on port %d\n", address, port);
            packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
            GameServer.addConnection(address, port);
        }
        System.out.println("Connection Listener killed.");
    }
    
    public void close() {
        running = false;
        socket.close();
    }
}
