package org.lordsofchaos.network;

import java.net.InetAddress;

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
}
