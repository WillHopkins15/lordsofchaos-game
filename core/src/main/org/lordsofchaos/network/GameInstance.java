package org.lordsofchaos.network;

import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.DataOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Thread for running an instance of the game over UDP.
 */
public class GameInstance extends UDPSocket
{
    private ConnectionPoint attacker;
    private ConnectionPoint defender;
    
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
    protected void setGameState(BuildPhaseData gameState) {
        this.gameState = gameState;
    }
    
    @Override
    protected void phaseChange() {
        send("Change Phase");
    }
}
