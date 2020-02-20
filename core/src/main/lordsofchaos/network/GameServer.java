package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Central game server, which keeps track of all of the current connections in a list
 * of pairs, which hold the connections local ip address and the port number it
 * is open on. The server tries to pair players up if there are enough to start a game
 * instance.
 *
 * @author Will Hopkins
 */
public class GameServer
{
    protected static final int SERV_PORT = 3333;
    protected static List<Pair<InetAddress, Integer>> connections = new ArrayList<>();
    
    public GameServer() {
    }
    
    @SneakyThrows
    public void run() {
        String servAddress = InetAddress.getLocalHost().getHostName();
        HostManager.addHost(servAddress);
        new ConnectionListener(SERV_PORT).start();
        System.out.printf("Server started on hostname %s\n", servAddress);
        
        while (true) {
            pairPlayers();
            Thread.sleep(1000);
        }
    }
    
    private static void pairPlayers() {
        if (getNumConnections() > 1) {
            Pair<InetAddress, Integer> player1 = connections.get(0);
            Pair<InetAddress, Integer> player2 = connections.get(1);
            
            new GameInstance(player1, player2).start();
            
            connections.remove(player1);
            connections.remove(player2);
        }
    }
    
    /**
     * Converts an object into a byte stream and sends it through an open UDP socket to
     * the given player.
     *
     * @param sock      Datagram socket to send packet through
     * @param recipient Local IP/port number pair of recipient
     * @param contents  Object to serialize and send
     */
    @SneakyThrows
    public static void sendPacket(DatagramSocket sock, Pair<InetAddress, Integer> recipient, Object contents) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(contents);
        oout.flush();
        byte[] bytes = bout.toByteArray();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, recipient.getKey(), recipient.getValue());
        sock.send(packet);
    }
    
    /**
     * @return List of current connections
     */
    public static List<Pair<InetAddress, Integer>> getConnections() {
        return connections;
    }
    
    /**
     * @return Number of current connections
     */
    public static int getNumConnections() {
        return connections.size();
    }
    
    protected static void addConnection(InetAddress address, int port) {
        connections.add(new Pair<>(address, port));
    }
    
    public static void main(String[] args) {
        new GameServer().run();
    }
}
