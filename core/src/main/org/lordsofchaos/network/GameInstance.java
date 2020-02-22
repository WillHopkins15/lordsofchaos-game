package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.DataOutputStream;
import java.net.*;

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
        connectAndSetTypes(attacker, defender);
    }
    
    /**
     * Guarantees a connection to the client on the same port by using TCP. This ensures that
     * the client will always receive its assigned type.
     *
     * @param attacker Inetaddress/port pair of client to make an attacker
     * @param defender Inetaddress/port pair of client to make a defender
     */
    @SneakyThrows
    private void connectAndSetTypes(Pair<InetAddress, Integer> attacker, Pair<InetAddress, Integer> defender) {
        //Switch socket to TCP
        int localPort = socket.getLocalPort();
        InetAddress localAddress = InetAddress.getLocalHost();
        socket.close();
        
        Socket socket = new Socket(attacker.getKey(), attacker.getValue(), localAddress, localPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF("Attacker");
        //Wait for receiver to close their end of the socket to avoid a TIME_WAIT which relys
        //on the kernel to release the port. A TIME_WAIT can last anywhere from 1 to 4 minutes.
        Thread.sleep(1000);
        out.close();
        socket.close();
        
        socket = new Socket(defender.getKey(), defender.getValue(), localAddress, localPort);
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
}
