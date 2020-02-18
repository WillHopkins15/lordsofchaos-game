package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.net.InetAddress;

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
        System.out.printf("Thread spawned on port %d\n", socket.getLocalPort());
        sendToPlayers("Echo");
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
    
    /**
     * Sends an object to both players who are connected to the server.
     *
     * @param contents Object to send
     */
    private void sendToPlayers(Object contents) {
        sendPacket(attacker, contents);
        sendPacket(defender, contents);
    }
    
    protected void createInputThread() {
        new Thread(() -> {
            while (running) {
                receiveData();
            }
        }).start();
    }
    
    protected void createOutputThread() {
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
}
