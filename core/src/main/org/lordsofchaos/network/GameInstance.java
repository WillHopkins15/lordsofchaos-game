package org.lordsofchaos.network;

import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
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
        //connectAndSetTypes(attacker, defender);
        sendObject(attacker, "Attacker");
        sendObject(defender, "Defender");
    }
    
    /**
     * Guarantees a connection to the client on the same port by using TCP. This ensures that
     * the client will always receive its assigned type.
     *
     * @param attacker Inetaddress/port pair of client to make an attacker
     * @param defender Inetaddress/port pair of client to make a defender
     */
    @SneakyThrows
    private void connectAndSetTypes(ConnectionPoint attacker, ConnectionPoint defender) {
        //Switch socket to TCP
        int localPort = socket.getLocalPort();
        InetAddress localAddress = InetAddress.getLocalHost();
        socket.close();
        
        Socket socket = new Socket(attacker.getAddress(), attacker.getPort(), localAddress, localPort);
        socket.setKeepAlive(false);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("Attacker");
        /*
        Wait for receiver to close their end of the socket to avoid a TIME_WAIT which relys
        on the kernel to release the port. A TIME_WAIT can last anywhere from 1 to 4 minutes.
        Info at https://hea-www.harvard.edu/~fine/Tech/addrinuse.html
         */
        Thread.sleep(1000);
        out.close();
        socket.close();
        
        socket = new Socket(defender.getAddress(), defender.getPort(), localAddress, localPort);
        socket.setKeepAlive(false);
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("Defender");
        out.close();
        socket.close();
        
        //Switch back to UDP
        this.socket = new DatagramSocket(localPort);
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
        NumberedPacket received = (NumberedPacket) getObjectFromBytes(packet.getData());
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
    
    private boolean validatePacket(NumberedPacket packetData, int sender) {
        if (sender == attacker.getPort()) {
            LocalDateTime newTime = LocalDateTime.parse(packetData.getTime());
            if (lastAttPacketTime == null) {
                lastAttPacketTime = newTime;
                return true;
            }
            if (newTime.isAfter(lastAttPacketTime)) {
                lastAttPacketTime = newTime;
                return true;
            }
        } else if (sender == defender.getPort()) {
            LocalDateTime newTime = LocalDateTime.parse(packetData.getTime());
            if (lastDefPacketTime == null) {
                lastDefPacketTime = newTime;
                return true;
            }
            if (newTime.isAfter(lastDefPacketTime)) {
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
