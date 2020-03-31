package org.lordsofchaos.network;

import java.net.InetAddress;

/**
 * Object that holds the local IP Address and port number of a socket open on a network;
 */
public class ConnectionPoint
{
    private InetAddress address;
    private int port;
    
    public ConnectionPoint(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String toString() {
        return "Address: " + address.toString() + " Port: " + port;
    }
}
