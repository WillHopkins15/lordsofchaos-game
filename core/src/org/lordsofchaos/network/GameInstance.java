package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;

/**
 * Thread for running an instance of the game over UDP.
 */
public class GameInstance extends Thread
{
    protected DatagramSocket socket;
    protected int threadPort;
    protected Pair<InetAddress, Integer> attacker;
    protected Pair<InetAddress, Integer> defender;
    private byte[] buffer = new byte[256];
    
    /**
     * Opens a new DatagramSocket on an available port for communication with the two players.
     *
     * @param player1 attacker
     * @param player2 defender
     */
    @SneakyThrows
    public GameInstance(Pair<InetAddress, Integer> player1, Pair<InetAddress, Integer> player2) {
        socket = new DatagramSocket();
        threadPort = socket.getLocalPort();
        attacker = player1;
        defender = player2;
        System.out.printf("Thread spawned on port %d\n", threadPort);
    }
    
    @SneakyThrows
    public void run() {
        boolean running = true;
        sendToPlayers("Connected");
        sendToPlayers("Starting game...");
        
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            socket.receive(packet);
            
            String received = (String) getObjectFromByteStream(packet.getData());
            System.out.printf("[%s] Message from %d: %s\n", LocalTime.now(), packet.getPort(), received);
            
            sendToPlayers("Recieved Game State");
        }
        socket.close();
    }
    
    @SneakyThrows
    private Object getObjectFromByteStream(byte[] bytes) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        return oin.readObject();
    }
    
    private void sendToPlayers(Object message) {
        GameServer.sendPacket(socket, attacker, message);
        GameServer.sendPacket(socket, defender, message);
    }
}
