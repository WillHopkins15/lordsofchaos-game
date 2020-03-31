package org.lordsofchaos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.SerializableTower;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.server.GameServer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class NetworkingTest
{
    private GameServer server;
    private GameClient player1;
    private GameClient player2;
    
    @Before
    public void setup() throws InterruptedException {
        System.out.println("Initiating Setup...");
        server = new GameServer();
        server.start();
        player1 = new GameClient();
        player2 = new GameClient();
        Thread.sleep(500);
    }
    
    @Test
    public void testGameStateSentOverNetworkAndUpdated() throws InterruptedException {
        //init game state
        int[][] units = new int[6][3];
        ArrayList<SerializableTower> tbp = new ArrayList<>();
        BuildPhaseData emptyGameState = new BuildPhaseData(units, tbp, new ArrayList<>(), 0, new ArrayList<>());
        
        //Connect both players to server
        new Thread(() -> player1.makeConnection()).start();
        new Thread(() -> player2.makeConnection()).start();
        
        //Wait for game instance to be set up
        Thread.sleep(2000);
        
        //Check if both clients have connected
        assertNotNull("Client1 not connected to server", player1.getServer());
        assertNotNull("Client2 not connected to server", player2.getServer());
        
        //kill central server to free up threads
        server.close();
        
        //set player2 to empty game state
        player2.setGameState(emptyGameState);
        
        //add things to player 1 game state
        tbp.add(new SerializableTower(TowerType.type1, new RealWorldCoordinates(12, 34)));
        BuildPhaseData gameState = new BuildPhaseData(units, tbp, new ArrayList<>(), 0, new ArrayList<>());
        
        //update player1 game state
        player1.setGameState(gameState);
        
        //send game state to server
        player1.send(gameState);
        
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
