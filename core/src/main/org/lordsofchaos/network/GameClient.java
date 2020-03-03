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
            //
            System.out.println("Server found!");
            System.out.println("Looking for opponent...");
            socket.setSoTimeout(0); //Stop socket from timing out
            socket.receive(packet);
            
            System.out.println("Found game.");
            playerType = (String) getObjectFromBytes(packet.getData());
            System.out.printf("[%d] Assigned to %s.\n", socket.getLocalPort(), playerType);
            //port number of connected server thread
            int port = packet.getPort();
            server = new ConnectionPoint(address, port);
            //

//            connectToServerAndGetPlayerType();

//            System.out.println("Found game.");
//            System.out.printf("[%d] Assigned to %s.\n", socket.getLocalPort(), playerType);
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
    
    @Override
    protected void phaseChange() {
        GameController.endPhase();
    }
    
    /**
     * Sends an object to the server
     *
     * @param contents Object to send
     */
    @Override
    public void send(Object contents) {
        sendObject(server, contents);
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
    
    @Override
    protected void createInputThread() {
        new Thread(() -> {
            while (running) {
                if (!isMyTurn()) {
                    receiveObject();
                }
            }
        }).start();
    }
    
    @Override
    protected void createOutputThread() {
        new Thread(() -> {
            while (running) {
                if (!getCurrentWave().equals("Play"))
                    send(gameState);
                else
                    send("Keep Alive");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }).start();
    }
}