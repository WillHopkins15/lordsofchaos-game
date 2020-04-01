package org.lordsofchaos.network;

import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;
import org.lordsofchaos.GameController;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.*;

/**
 * Client which periodically sends game data over UDP to a server running a game
 * instance.
 *
 * @author Will Hopkins
 */
public class GameClient extends UDPSocket
{
    private ConnectionPoint server;
    private byte[] buffer = new byte[256];
    private String playerType = "";
    private boolean connected = false;
    
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
        if (connected) {
            return false;
        }
        socket.setSoTimeout(5000);
        DatagramPacket packet;
        for (String item : HostManager.getHosts()) {
            InetAddress address;
            try {
                address = InetAddress.getByName(item);
                packet = new DatagramPacket(buffer, buffer.length, address, 5148);
                socket.send(packet);
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                System.out.printf("Host %s not available.\n", item);
                continue;
            } catch (UnknownHostException e) {
                System.out.printf("Host %s not found.\n", item);
                continue;
            }
            System.out.println("Server Found.\nAwaiting Opponent.");
            
            connectToServerAndGetPlayerType();

            System.out.println("Opponent Found.");
            System.out.printf("[%d] Assigned to %s.\n", socket.getLocalPort(), playerType);
    
            // Get confirmation that the other client is ready
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                System.out.println("Opponent disconnected.");
                return false;
            }
            connected = true;
            return true;
        }
        System.out.println("No Servers Online.");
        return false;
    }
    
    /**
     * Temporarily switch to TCP in order to guarantee a connection with the server, and that
     * this players type will get set correctly.
     */
    @SneakyThrows
    private void connectToServerAndGetPlayerType() {
        //Temporarily switch to TCP
        int port = socket.getLocalPort();
        socket.close();
        
        ServerSocket serv = new ServerSocket(port);
        serv.setSoTimeout(0);
        Socket tcpsock = serv.accept();
        DataInputStream in = new DataInputStream(new BufferedInputStream(tcpsock.getInputStream()));
        
        server = new ConnectionPoint(tcpsock.getInetAddress(), tcpsock.getPort());
        playerType = in.readUTF();
        
        in.close();
        serv.close();
        tcpsock.close();
        
        //switch back
        socket = new DatagramSocket(port);
    }
    
    @SneakyThrows
    public void run() {
        System.out.printf("Connected to %s on port %d\n", server.getAddress(), server.getPort());
        socket.setSoTimeout(500);
        createInputThread();
        createOutputThread();
        
        while (running) {
            this.gameState = GameController.getGameState();
            Thread.sleep(100);
        }
    }
    
    /**
     * Sends an object to the server.
     * @param contents Object to send
     * @param numbered Whether the packet should be time stamped or not
     */
    public void send(Object contents, boolean numbered) {
        if (numbered) {
            send(contents);
        } else {
            sendObject(server, contents);
        }
    }
    
    /**
     * Sends an object to the server as a time stamped packet
     * @param contents Object to send
     */
    @Override
    public void send(Object contents) {
        sendObject(server, new NumberedPacket(contents));
    }
    
    @Override
    protected void parsePacket(DatagramPacket packet) {
        Object received = getObjectFromBytes(packet.getData());
        if (received == null) {
            System.out.printf("[%d] Received null from %d\n", socket.getLocalPort(), packet.getPort());
        } else if (received.getClass() == String.class) {
            System.out.printf("[%d] Message from %d: %s\n", socket.getLocalPort(), packet.getPort(), received);
            if (received.equals("Change Phase")) {
                GameController.endPhase();
            }
        } else if (received.getClass() == BuildPhaseData.class) {
            System.out.printf("[%d] Received game state\n", socket.getLocalPort());
            gameState = (BuildPhaseData) received;
            setGameState(gameState);
        }
    }
    
    /**
     * @return InetAdress/Port number pair of connected server. Null if not connected
     */
    public ConnectionPoint getServer() {
        return server;
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
        GameController.setGameState(this.gameState);
    }
    
    /**
     * @return Assigned player type - either Attacker or Defender
     */
    public String getPlayerType() {
        return this.playerType;
    }
    
    /**
     * @return Whether this player type is an attacker.
     */
    public boolean isAttacker() {
        return this.getPlayerType().equals("Attacker");
    }
    
    /**
     * @return Whether this player type is a defender.
     */
    public boolean isDefender() {
        return this.getPlayerType().equals("Defender");
    }
    
    /**
     * @return if the client has been connected to a server
     */
    public boolean isConnected() {
        return connected;
    }
    /**
     * @return Current wave state
     */
    public String getCurrentWave() {
        return GameController.getWaveState().toString();
    }
    /**
     * @return Whether it is this players turn.
     */
    public boolean isMyTurn() {
        return getCurrentWave().equals(getPlayerType() + "Build");
    }
}