package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Client which periodically sends game data over UDP to a server running a game
 * instance.
 *
 * @author Will Hopkins
 */
public class GameClient extends UDPSocket
{
    public int port = 0; //port number of connected server thread
    private InetAddress address; //address of connected server thread
    private Pair<InetAddress, Integer> server;
    
    /**
     * Creates a UDP Datagram socket on an available port.
     */
    public GameClient() {
        super();
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
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, GameServer.SERV_PORT);
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
            port = packet.getPort();
            
            System.out.println("Found game.");
            return true;
        }
        System.out.println("No Servers Online.");
        return false;
    }
    
    @SneakyThrows
    public void run() {
        server = new Pair<>(address, port);
        System.out.printf("Connected to %s on port %d\n", server.getKey(), server.getValue());
        socket.setSoTimeout(1000);
        createInputThread();
        createOutputThread();
        
        while (running) {
        
        }
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
                sendToServer(gameState);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }).start();
    }
    
    /**
     * Sends an object to the server
     *
     * @param contents Object to send
     */
    private void sendToServer(Object contents) {
        sendPacket(server, contents);
    }
    
    /**
     * @return Current game state
     */
    public BuildPhaseData getGameState() {
        return this.gameState;
    }
    
    /**
     * Updates the current game state
     *
     * @param state New game state
     */
    public void setGameState(BuildPhaseData state) {
        this.gameState = state;
    }
}
