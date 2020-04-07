package org.lordsofchaos.network;

import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.time.LocalDateTime;

/**
 * Thread for running an instance of the game over UDP.
 */
public class GameInstance extends UDPSocket
{
    private ConnectionPoint attacker;
    private ConnectionPoint defender;
    private LocalDateTime lastAttPacketTime = null;
    private LocalDateTime lastDefPacketTime = null;
    
    /**
     * Opens a new DatagramSocket on an available port for communication with the two players.
     *
     * @param player1 attacker
     * @param player2 defender
     */
    @SneakyThrows
    public GameInstance(ConnectionPoint player1, ConnectionPoint player2) {
        super();
        attacker = player1;
        defender = player2;
        System.out.printf("Thread spawned on port %d\n", socket.getLocalPort());
        if (connectAndSetTypes(attacker, defender)) return;
        send("mismatched maps");
        running = false;
    }
    
    /**
     * Guarantees a connection to the client on the same port by using TCP. This ensures that
     * the client will always receive its assigned type. This method gets the clients selected
     * map, and returns whether both clients maps are the same.
     *
     * @param attacker Inetaddress/port pair of client to make an attacker
     * @param defender Inetaddress/port pair of client to make a defender
     * @return Whether or not the clients maps are the same
     */
    @SneakyThrows
    private boolean connectAndSetTypes(ConnectionPoint attacker, ConnectionPoint defender) {
        //Switch socket to TCP
        int localPort = socket.getLocalPort();
        InetAddress localAddress = InetAddress.getLocalHost();
        socket.close();
    
        Socket attSocket = null;
        while (attSocket == null) {
            try {
                attSocket = new Socket(attacker.getAddress(), attacker.getPort(), localAddress, localPort);
            } catch (BindException e) {
                Thread.sleep(1000);
            }
        }
        DataInputStream in = new DataInputStream(attSocket.getInputStream());
        String attackerMap = in.readUTF();
        DataOutputStream out = new DataOutputStream(attSocket.getOutputStream());
        out.writeUTF("Attacker");
        /*
        Wait for receiver to close their end of the socket to avoid a TIME_WAIT which relys
        on the kernel to release the port. A TIME_WAIT can last anywhere from 1 to 4 minutes.
        Info at https://hea-www.harvard.edu/~fine/Tech/addrinuse.html
         */
        Thread.sleep(3000);
        out.close();
        attSocket.close();
    
        Socket defSocket = null;
        while (defSocket == null) {
            try {
                defSocket = new Socket(defender.getAddress(), defender.getPort(), localAddress, localPort);
            } catch (BindException e) {
                Thread.sleep(1000);
            }
        }
        in = new DataInputStream(defSocket.getInputStream());
        String defenderMap = in.readUTF();
        out = new DataOutputStream(attSocket.getOutputStream());
        out.writeUTF("Defender");
        out.close();
        attSocket.close();
        
        //Switch back to UDP
        this.socket = new DatagramSocket(localPort);
        return attackerMap.equals(defenderMap);
    }
    
    @SneakyThrows
    public void run() {
        send("Starting game...");
        
        socket.setSoTimeout(500);
        createInputThread();
        createOutputThread();
        
        while (running) {
        
        }
        System.out.println("Game Instance Closed");
    }
    
    /**
     * Sends an object to both players who are connected to the server.
     *
     * @param contents Object to send
     */
    @Override
    protected void send(Object contents) {
        sendObject(attacker, contents);
        sendObject(defender, contents);
    }
    
    @Override
    protected void parsePacket(DatagramPacket packet) {
        int senderPort = packet.getPort();
        TimestampedPacket received = (TimestampedPacket) getObjectFromBytes(packet.getData());
        Object data = received.getData();
        if (data == null) {
            System.out.printf("[%d] Received null from %d\n", socket.getLocalPort(), senderPort);
        } else if (data.getClass() == String.class) {
            System.out.printf("[%d] Message from %d: %s\n", socket.getLocalPort(), senderPort, data);
            if (data.equals("Change Phase")) {
                System.out.printf("[%d] Sending Phase Change\n", socket.getLocalPort());
                send("Change Phase");
            }
        } else if (data.getClass() == BuildPhaseData.class) {
            if (!validatePacket(received, senderPort)) return;
            System.out.printf("[%d] Received game state\n", socket.getLocalPort());
            if (((BuildPhaseData) data).getCurrentWave().equals("AttackerBuild")
                    && senderPort == defender.getPort()) {
                return;
            } else if (((BuildPhaseData) data).getCurrentWave().equals("DefenderBuild")
                    && senderPort == attacker.getPort()) {
                return;
            }
            setGameState((BuildPhaseData) data);
        }
    }
    
    /**
     * Checks whether the packet received is the most recent packet sent.
     * @param packetData timestamped packet
     * @param sender port number of the sender
     * @return true if the packet is valid
     */
    private boolean validatePacket(TimestampedPacket packetData, int sender) {
        if (sender == attacker.getPort()) {
            LocalDateTime newTime = LocalDateTime.parse(packetData.getTime());
            if (lastAttPacketTime == null || newTime.isAfter(lastAttPacketTime)) {
                lastAttPacketTime = newTime;
                return true;
            }
        } else if (sender == defender.getPort()) {
            LocalDateTime newTime = LocalDateTime.parse(packetData.getTime());
            if (lastDefPacketTime == null || newTime.isAfter(lastDefPacketTime)) {
                lastDefPacketTime = newTime;
                return true;
            }
        }
        System.out.printf("[%d] Game state expired.", socket.getLocalPort());
        return false;
    }
    
    protected void setGameState(BuildPhaseData gameState) {
        this.gameState = gameState;
    }
}
