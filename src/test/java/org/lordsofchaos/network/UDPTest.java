package org.lordsofchaos.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UDPTest {
    GameClient client;

    @Before
    public void setup(){
        new GameServer().start();
        client = new GameClient();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        String echo = client.sendEcho("hello server");
        assertEquals("hello server", echo);
        echo = client.sendEcho("server is working");
        assertNotEquals("hello server", echo);
    }

    @After
    public void tearDown() {
        client.sendEcho("end");
        client.close();
    }
}
