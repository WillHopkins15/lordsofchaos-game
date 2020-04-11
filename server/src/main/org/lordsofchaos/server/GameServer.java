package org.lordsofchaos.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.lordsofchaos.network.ConnectionPoint;
import org.lordsofchaos.network.GameInstance;
import org.lordsofchaos.network.HostManager;

/**
 * Central game server, which keeps track of all of the current connections in a list of pairs,
 * which hold the connections local ip address and the port number it is open on. The server tries
 * to pair players up if there are enough to start a game instance.
 *
 * @author Will Hopkins
 */
public class GameServer extends Thread {

    protected final int SERV_PORT = 5148;
    private List<ConnectionPoint> connections = new ArrayList<>();
    private ConnectionListener connectionListener;
    private volatile boolean running = true;

    public GameServer() {
    }

    public static void main(String[] args) {
        new GameServer().start();
    }

    /**
     * Gets the first two connections from the list and creates a game between them.
     */
    private void pairPlayers() {
        if (getNumConnections() > 1) {
            ConnectionPoint player1 = connections.get(0);
            ConnectionPoint player2 = connections.get(1);

            new GameInstance(player1, player2).start();

            connections.remove(player1);
            connections.remove(player2);
        }
    }

    /**
     * @return Number of current connections
     */
    protected int getNumConnections() {
        return connections.size();
    }

    /**
     * Adds a new connection to the list of current connections.
     *
     * @param address InetAddress of connection
     * @param port    Port number the connection is currently open on
     */
    protected void addConnection(InetAddress address, int port) {
        connections.add(new ConnectionPoint(address, port));
    }

    /**
     * Stops the running server and any threads it has produced.
     */
    public void close() {
        connectionListener.close();
        running = false;
    }

    @SneakyThrows
    public void run() {
        String servAddress = InetAddress.getLocalHost().getHostName();
        if (!HostManager.hostRecognised(servAddress)) {
            return;
        }
        connectionListener = new ConnectionListener(SERV_PORT);
        connectionListener.start();
        System.out.printf("Server started on %s\n", servAddress);

        running = true;
        while (running) {
            pairPlayers();
            Thread.sleep(1000);
        }
    }

    /**
     * Thread for listening to new UDP connections on the specified port number. New connections are
     * added to a list in the {@link GameServer} class.
     */
    private class ConnectionListener extends Thread {

        private byte[] buffer = new byte[256];
        private DatagramSocket socket;

        /**
         * Creates a UDP DatagramSocket on the specified port, if the port is available.
         *
         * @param port Port number to open on
         */
        @SneakyThrows
        public ConnectionListener(int port) {
            socket = new DatagramSocket(port);
        }

        @SneakyThrows
        public void run() {
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (SocketException ignored) {
                    return;
                }
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                System.out
                    .printf("Connection request received from %s on port %d\n", address, port);
                packet = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(packet);
                addConnection(address, port);
            }
            System.out.println("Connection Listener killed.");
        }

        /**
         * Stops the running thread and closes the socket.
         */
        public void close() {
            running = false;
            socket.close();
        }
    }
}