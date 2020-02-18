package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

/**
 * Class that abstracts sending and receiving objects over a DatagramSocket.
 *
 * @author Will Hopkins
 */
public abstract class UDPSocket extends Thread
{
    private static final Object LOCK = new Object();
    protected boolean running = true;
    protected DatagramSocket socket;
    protected BuildPhaseData gameState = null;
    protected byte[] buffer = new byte[1024];
    
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
    protected void sendPacket(Pair<InetAddress, Integer> recipient, Object contents) {
        if (!socket.isClosed()) {
            synchronized (LOCK) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(bout);
                oout.writeObject(contents);
                byte[] bytes = bout.toByteArray();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, recipient.getKey(), recipient.getValue());
                socket.send(packet);
            }
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
        synchronized (LOCK) {
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
    }
    
    /**
     * Listens on the open socket for a packet containing serialized object data.
     * If the object received is a String, then it logs the message to the console.
     * If the object is a game state, then this method updates the clients stored
     * game state data.
     */
    @SneakyThrows
    protected void receiveData() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        try {
            socket.receive(packet);
        } catch (SocketException e) {
            System.out.println("Socket closed, ending...");
            return;
        } catch (SocketTimeoutException e) {
            System.out.println("Receive timed out.");
        }
        
        Object received = getObjectFromBytes(packet.getData());
        if (received == null) {
            System.out.printf("Received null from %d\n", packet.getPort());
        } else if (received.getClass() == String.class) {
            System.out.printf("[%s] Message from %d: %s\n", LocalTime.now(), packet.getPort(), received);
        } else if (received.getClass() == BuildPhaseData.class) {
            System.out.printf("[%d] Received game state\n", socket.getLocalPort());
            gameState = (BuildPhaseData) received;
        }
    }
    
    /**
     * Creates a thread for listening for Datagram packets and processing the data within.
     */
    protected abstract void createInputThread();
    
    /**
     * Creates a thread for sending Datagram packets concurrently.
     */
    protected abstract void createOutputThread();
    
    public abstract void run();
    
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
