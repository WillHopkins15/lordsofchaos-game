package org.lordsofchaos.network;

import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Class that abstracts sending and receiving objects over a DatagramSocket.
 *
 * @author Will Hopkins
 */
public abstract class UDPSocket extends Thread
{
    protected volatile boolean running = true;
    protected DatagramSocket socket;
    protected BuildPhaseData gameState = null;
    private byte[] buffer = new byte[1024]; //Needs to be big enough to hold the game state object
    private int timeoutCount = 0;
    
    /**
     * Creates a UDP Datagram socket on an available port.
     */
    @SneakyThrows
    protected UDPSocket() {
        socket = new DatagramSocket();
    }
    
    /**
     * Converts an object into a byte stream and sends it through an open UDP socket to
     * the given player.
     *
     * @param recipient Local IP/port number pair of recipient
     * @param contents  Object to serialize and send
     */
    @SneakyThrows
    protected void sendObject(ConnectionPoint recipient, Object contents) {
        if (!socket.isClosed()) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(contents);
            byte[] bytes = bout.toByteArray();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, recipient.getAddress(), recipient.getPort());
            socket.send(packet);
        } else {
            System.out.println("Packet not sent: Socket closed");
        }
    }
    
    /**
     * De-serializes byte array back into an object. Byte array must have been
     * serialized by an ObjectOutputStream.
     *
     * @param bytes Byte array to convert
     * @return Resulting deserialized object
     */
    @SneakyThrows
    protected Object getObjectFromBytes(byte[] bytes) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = new ObjectInputStream(bin);
        Object obj = null;
        try {
            obj = oin.readObject();
        } catch (EOFException ex) {
            System.out.println("EOF");
        }
        return obj;
    }
    
    /**
     * Listens on the open socket for a packet containing serialized object data.
     * If the object received is a String, then it logs the message to the console.
     * If the object is a game state, then this method updates the clients stored
     * game state data.
     */
    @SneakyThrows
    protected void receiveObject() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        try {
            socket.receive(packet);
        } catch (SocketException e) {
            System.out.println("Socket closed, ending...");
            return;
        } catch (SocketTimeoutException e) {
            System.out.printf("[%d] Receive timed out.\n", socket.getLocalPort());
            timeoutCount++;
            if (timeoutCount >= 10) {
                System.out.println("Connection dropped. Closing...");
                this.close();
            }
            return;
        }
        
        timeoutCount = 0;
        Object received = getObjectFromBytes(packet.getData());
        if (received == null) {
            System.out.printf("[%d] Received null from %d\n", socket.getLocalPort(), packet.getPort());
        } else if (received.getClass() == String.class) {
            System.out.printf("[%d] Message from %d: %s\n", socket.getLocalPort(), packet.getPort(), received);
        } else if (received.getClass() == BuildPhaseData.class) {
            System.out.printf("[%d] Received game state\n", socket.getLocalPort());
            gameState = (BuildPhaseData) received;
            setGameState(gameState);
            System.out.println(gameState.toString());
        }
    }
    
    /**
     * Creates a thread for listening for Datagram packets and processing the data within.
     */
    protected void createInputThread() {
        new Thread(() -> {
            while (running) {
                receiveObject();
            }
        }).start();
    }
    
    /**
     * Creates a thread for sending Datagram packets concurrently.
     */
    protected void createOutputThread() {
        new Thread(() -> {
            while (running) {
                send(gameState);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }).start();
    }
    
    public abstract void run();
    
    protected abstract void send(Object contents);
    
    protected abstract void setGameState(BuildPhaseData gameState);
    
    /**
     * Closes the socket if open and kills any open threads.
     */
    public void close() {
        running = false;
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
