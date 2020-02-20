package org.lordsofchaos.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lordsofchaos.BuildPhaseData;
import org.lordsofchaos.EventManager;

import java.io.File;
import java.util.ArrayList;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class NetworkingTest
{
    private GameServer server;
    private GameClient player1;
    private GameClient player2;
    
    @Before
    public void setup() throws InterruptedException {
        System.out.println("Initiating Setup...");
        System.setProperty("user.dir", new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath());
        server = new GameServer();
        server.start();
        player1 = new GameClient();
        player2 = new GameClient();
        Thread.sleep(500);
    }
    
    @Test
    public void testGameStateSentOverNetworkAndUpdated() throws InterruptedException {
        BuildPhaseData gameState;
        //init game state
        int[][] units = new int[6][3];
        ArrayList<EventManager.TowerBuild> tbp = new ArrayList<>();
        gameState = new BuildPhaseData(units, tbp);
        
        //Connect both players to server and start
        new Thread(() -> {
            if (player1.makeConnection()) {
                //player1.setGameState(finalGameState);
                player1.start();
            }
        }).start();
        
        new Thread(() -> {
            if (player2.makeConnection()) {
                player2.start();
            }
        }).start();
        
        //Wait for game instance to be set up
        int port1 = 0;
        int port2 = 0;
        try {
            port1 = player1.getServer().getValue();
            port2 = player2.getServer().getValue();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        assertNotEquals("Client1 not connected to server", 0, port1);
        assertNotEquals("Client2 not connected to server", 0, port2);
        
        //kill central server to free up threads
        server.close();
        
        //update player1 game state
        player1.setGameState(gameState);
        
        //wait for clients to update
        Thread.sleep(1000);
        
        //Check if gameState updated on both clients
        assertThat(gameState, samePropertyValuesAs(player1.getGameState()));
        assertThat(gameState, samePropertyValuesAs(player2.getGameState()));
    }
    
    @After
    public void cleanup() {
        System.out.println("Initiating Cleanup...");
        if (server.isAlive()) {
            server.close();
        }
        player1.close();
        player2.close();
    }
}
