package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameInstance extends Thread
{
    protected DatagramSocket socket;
    protected int threadPort;
    protected Pair<InetAddress, Integer> attacker;
    protected Pair<InetAddress, Integer> defender;
    private byte[] buffer = new byte[256];
    
    public GameInstance(Pair<InetAddress, Integer> player1, Pair<InetAddress, Integer> player2) {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        threadPort = socket.getLocalPort();
        attacker = player1;
        defender = player2;
        System.out.printf("Thread spawned on port %d\n", threadPort);
    }
    
    @SneakyThrows
    public void run() {
        boolean running = true;
        sendToAllPlayers("Connected");
        
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            socket.receive(packet);
            
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.printf("[%d]Message: %s\n", packet.getPort(), received);
        }
        socket.close();
    }
    
    private void sendPacket(Pair<InetAddress, Integer> player, String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, player.getKey(), player.getValue());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendToAllPlayers(String message) {
        sendPacket(attacker, message);
        sendPacket(defender, message);
    }
}
