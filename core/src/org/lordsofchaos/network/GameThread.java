package org.lordsofchaos.network;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameThread extends Thread
{
    protected DatagramSocket socket;
    protected InetAddress clientAddress;
    protected int clientPort;
    protected int threadPort;
    private byte[] buffer = new byte[256];
    
    /**
     * Creates a new UDP DatagramSocket on the specified port
     *
     * @param cAddress Address of the client
     * @param cPort    Port the client is listening on
     */
    public GameThread(InetAddress cAddress, int cPort) {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.clientAddress = cAddress;
        this.clientPort = cPort;
        this.threadPort = this.socket.getLocalPort();
        System.out.printf("Socket set up on port %d\n", this.threadPort);
    }
    
    @SneakyThrows
    public void run() {
        boolean running = true;
        sendPacket("Connected");
        
        while (running) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            socket.receive(packet);
            
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Message: " + received);
            
            if (received.equals("end")) {
                running = false;
            }
        }
        socket.close();
    }
    
    private void sendPacket(String message) {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.clientAddress, this.clientPort);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
