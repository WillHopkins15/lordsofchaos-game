package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

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
public class GameServer extends Thread
{
    public static final int SERV_PORT = 5148;
    protected static List<Pair<InetAddress, Integer>> connections = new ArrayList<>();
    private static ConnectionListener connectionListener;
    public boolean running = true;
    
    public GameServer() {
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
        new GameServer().start();
    }
    
    public void close() {
        connectionListener.running = false;
        running = false;
    }
    
    @SneakyThrows
    public void run() {
        String servAddress = InetAddress.getLocalHost().getHostName();
        HostManager.addHost(servAddress);
        connectionListener = new ConnectionListener(SERV_PORT);
        connectionListener.start();
        System.out.printf("Server started on %s\n", servAddress);
        
        running = true;
        while (running) {
            pairPlayers();
            Thread.sleep(1000);
        }
    }
}