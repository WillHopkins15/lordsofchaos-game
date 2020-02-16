package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalTime;

/**
 * Client which periodically sends game data over UDP to a server. Contains
 * methods which handle sent and received packet data.
 *
 * @author Will Hopkins
 */
public class GameClient
{
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf = new byte[256];
    private int port;
    
    /**
     * Creates a UDP Datagram socket on an available port.
     */
    @SneakyThrows
    public GameClient() {
        socket = new DatagramSocket();
    }
    
    @SneakyThrows
    private static Object getObjectFromPacket(byte[] bytes) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        return oin.readObject();
    }
    
    /**
     * Filters through the knownhosts file to find an online server. Fails if
     * no servers are online. When a connection is made to a server, the method
     * blocks until the server pairs this client with another. Intended to be used
     * when the user chooses to look for a match.
     *
     * @return true if connection made successfully, false otherwise
     */
    @SneakyThrows
    public boolean makeConnection() {
        socket.setSoTimeout(5000);
        for (String item : HostManager.getHosts()) {
            address = InetAddress.getByName(item);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, GameServer.SERV_PORT);
            socket.send(packet);
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                System.out.printf("Host %s not available.\n", address);
                continue;
            }
            
            System.out.println("Server found!");
            System.out.println("Looking for opponent...");
            socket.setSoTimeout(0); //Stop socket from timing out
            socket.receive(packet);
            
            getObjectFromPacket(packet.getData());
            port = packet.getPort();
            System.out.printf("Connected to %s on port %d\n", address, port);
            return true;
        }
        System.out.println("No Servers Online.");
        return false;
    }
    
    @SneakyThrows
    public void runGame() {
        Pair<InetAddress, Integer> server = new Pair<>(address, port);
        while (true) {
            //send game state
            String gameState = "Sent Game State";
            GameServer.sendPacket(socket, server, gameState);
            
            //receive game state
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.receive(packet);
            Object ob = getObjectFromPacket(packet.getData());
            if (ob.getClass() == String.class) {
                System.out.printf("[%s]Message from server: %s\n", LocalTime.now(), ob);
            } else {
                //Update game state
            }
            //synchronize
            Thread.sleep(1000);
        }
    }
    
    public void close() {
        socket.close();
    }
}
