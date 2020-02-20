package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Client which periodically sends game data over UDP to a server running a game
 * instance.
 *
 * @author Will Hopkins
 */
public class GameClient extends UDPSocket
{
    private Pair<InetAddress, Integer> server;
    private byte[] buffer = new byte[256];
    private String playerType = "";
    
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
        DatagramPacket packet;
        for (String item : HostManager.getHosts()) {
            InetAddress address;
            try {
                address = InetAddress.getByName(item);
                packet = new DatagramPacket(buffer, buffer.length, address, GameServer.SERV_PORT);
                socket.send(packet);
                socket.receive(packet);
            } catch (SocketTimeoutException | UnknownHostException e) {
                System.out.printf("Host %s not available.\n", item);
                continue;
            }
            
            System.out.println("Server found!");
            System.out.println("Looking for opponent...");
            socket.setSoTimeout(0); //Stop socket from timing out
            socket.receive(packet);
            
            System.out.println("Found game.");
            playerType = (String) getObjectFromBytes(packet.getData());
            System.out.printf("[%d] Assigned to %s.\n", socket.getLocalPort(), playerType);
            //port number of connected server thread
            int port = packet.getPort();
            server = new Pair<>(address, port);
            return true;
        }
        System.out.println("No Servers Online.");
        return false;
    }
    
    @SneakyThrows
    public void run() {
        System.out.printf("Connected to %s on port %d\n", server.getKey(), server.getValue());
        socket.setSoTimeout(1000);
        createInputThread();
        createOutputThread();
        
        while (running) {
        
        }
    }
    
    /**
     * Sends an object to the server
     *
     * @param contents Object to send
     */
    @Override
    protected void send(Object contents) {
        sendObject(server, contents);
    }
    
    /**
     * @return Current game state
     */
    public BuildPhaseData getGameState() {
        return gameState;
    }
    
    /**
     * Updates the current game state
     *
     * @param gameState New game state
     */
    public void setGameState(BuildPhaseData gameState) {
        this.gameState = gameState;
    }
    
    /**
     * @return Assigned player type - either Attacker or Defender
     */
    public String getPlayerType() {
        return this.playerType;
    }
    
    /**
     * @return InetAdress/Port number pair of connected server
     * @throws Exception If client has not connected to a server.
     */
    public Pair<InetAddress, Integer> getServer() throws Exception {
        if (server == null) {
            throw new Exception("Connection to a server has not been made.");
        }
        return this.server;
    }
}
