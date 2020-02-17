package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalTime;

/**
 * Thread for running an instance of the game over UDP.
 */
public class GameInstance extends UDPSocket
{
    private Pair<InetAddress, Integer> attacker;
    private Pair<InetAddress, Integer> defender;
    
    /**
     * Opens a new DatagramSocket on an available port for communication with the two players.
     *
     * @param player1 attacker
     * @param player2 defender
     */
    @SneakyThrows
    public GameInstance(Pair<InetAddress, Integer> player1, Pair<InetAddress, Integer> player2) {
        super();
        attacker = player1;
        defender = player2;
        int threadPort = socket.getLocalPort();
        System.out.printf("Thread spawned on port %d\n", threadPort);
        sendToPlayers("Echo");
    }
    
    public void close() {
        running = false;
        socket.close();
    }
    
    @SneakyThrows
    public void run() {
        sendToPlayers("Connected");
        sendToPlayers("Starting game...");
        
        socket.setSoTimeout(500);
        createInputThread();
        createOutputThread();
        
        while (running) {
        
        }
    }
    
    protected void sendToPlayers(Object message) {
        sendPacket(attacker, message);
        sendPacket(defender, message);
    }
    
    public void createInputThread() {
        new Thread(() -> {
            while (running) {
                receiveData();
            }
        }).start();
    }
    
    public void createOutputThread() {
        new Thread(() -> {
            while (running) {
                sendToPlayers(gameState);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }).start();
    }
    
    @SneakyThrows
    private void receiveData() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            System.out.println("Receive timed out");
        }
        
        Object received = getObjectFromBytes(packet.getData());
        if (received == null) {
            System.out.printf("Received null from %d\n", packet.getPort());
        } else if (received.getClass() == String.class) {
            System.out.printf("[%s] Message from %d: %s\n", LocalTime.now(), packet.getPort(), received);
        } else if (received.getClass() == BuildPhaseData.class) {
            System.out.printf("[%d] Received game state\n", socket.getLocalPort());
            gameState = (BuildPhaseData) received;
        }
    }
}
