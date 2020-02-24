package org.lordsofchaos.server;

import lombok.SneakyThrows;
import org.lordsofchaos.network.ConnectionPoint;
import org.lordsofchaos.network.GameInstance;
import org.lordsofchaos.network.HostManager;

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
    protected static final int SERV_PORT = 5148;
    private static List<ConnectionPoint> connections = new ArrayList<>();
    private static ConnectionListener connectionListener;
    private boolean running = true;
    
    public GameServer() {
    }
    
    private static void pairPlayers() {
        if (getNumConnections() > 1) {
            ConnectionPoint player1 = connections.get(0);
            ConnectionPoint player2 = connections.get(1);
            
            new GameInstance(player1, player2).start();
            
            connections.remove(player1);
            connections.remove(player2);
        }
    }
    
    /**
     * @return List of current connections
     */
    public static List<ConnectionPoint> getConnections() {
        return connections;
    }
    
    /**
     * @return Number of current connections
     */
    public static int getNumConnections() {
        return connections.size();
    }
    
    protected static void addConnection(InetAddress address, int port) {
        connections.add(new ConnectionPoint(address, port));
    }
    
    public static void main(String[] args) {
        new GameServer().start();
    }
    
    public void close() {
        connectionListener.close();
        running = false;
    }
    
    @SneakyThrows
    public void run() {
        String servAddress = InetAddress.getLocalHost().getHostName();
        if (!HostManager.hostRecognised(servAddress)) {
            return;
        }
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