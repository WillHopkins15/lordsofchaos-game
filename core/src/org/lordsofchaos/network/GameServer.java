package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class GameServer
{
    protected static final int SERV_PORT = 3333;
    protected static List<Pair<InetAddress, Integer>> connections = new ArrayList<>();
    
    @SneakyThrows
    public static void main(String[] args) {
        String servAddress = InetAddress.getLocalHost().getHostName();
        HostManager.addHost(servAddress);
        new ConnectionListener(SERV_PORT).start();
        System.out.printf("Server started on hostname %s\n", servAddress);
        
        while (true) {
            pairPlayers();
            Thread.sleep(1000);
        }
    }
    
    public static void pairPlayers() {
        if (getNumConnections() > 1) {
            Pair<InetAddress, Integer> player1 = connections.get(0);
            Pair<InetAddress, Integer> player2 = connections.get(1);
            
            new GameInstance(player1, player2).start();
            
            connections.remove(player1);
            connections.remove(player2);
        }
    }
    
    public static List<Pair<InetAddress, Integer>> getConnections() {
        return connections;
    }
    
    public static int getNumConnections() {
        return connections.size();
    }
    
    public static void addConnection(InetAddress address, int port) {
        connections.add(new Pair<>(address, port));
    }
}
