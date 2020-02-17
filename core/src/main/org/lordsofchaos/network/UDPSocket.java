package org.lordsofchaos.network;

import javafx.util.Pair;
import lombok.SneakyThrows;
import org.lordsofchaos.BuildPhaseData;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class UDPSocket extends Thread
{
    private static ByteArrayInputStream bin;
    private static ByteArrayOutputStream bout;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    private static final Object LOCK = new Object();
    protected boolean running = true;
    protected DatagramSocket socket;
    protected BuildPhaseData gameState;
    protected byte[] buffer = new byte[1024];
    
    /**
     * Creates a UDP Datagram socket on an available port.
     */
    @SneakyThrows
    protected UDPSocket() {
        socket = new DatagramSocket();
        bout = new ByteArrayOutputStream();
        oout = new ObjectOutputStream(bout);
        oout.writeObject("Hello");
        bin = new ByteArrayInputStream(bout.toByteArray());
        oin = new ObjectInputStream(bin);
        gameState = null;
    }
    
    /**
     * Converts an object into a byte stream and sends it through an open UDP socket to
     * the given player.
     *
     * @param recipient Local IP/port number pair of recipient
     * @param contents  Object to serialize and send
     */
    @SneakyThrows
    public void sendPacket(Pair<InetAddress, Integer> recipient, Object contents) {
        if (!socket.isClosed()) {
            synchronized (LOCK) {
                bout = new ByteArrayOutputStream();
                oout = new ObjectOutputStream(bout);
                oout.writeObject(contents);
                byte[] bytes = bout.toByteArray();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, recipient.getKey(), recipient.getValue());
                socket.send(packet);
            }
        } else {
            System.out.println("Packet not sent: Socket closed");
        }
    }
    
    @SneakyThrows
    public Object getObjectFromBytes(byte[] bytes) {
        synchronized (LOCK) {
            bin = new ByteArrayInputStream(bytes);
            oin.close();
            oin = new ObjectInputStream(bin);
            Object obj = null;
            try {
                obj = oin.readObject();
            } catch (EOFException ex) {
                System.out.println("EOF");
            }
            return obj;
        }
    }
    
    abstract void createInputThread();
    
    abstract void createOutputThread();
    
    public abstract void run();
}
