package org.lordsofchaos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.SerializableTower;
import org.lordsofchaos.network.GameClient;
import org.lordsofchaos.network.TimestampedPacket;

import java.util.ArrayList;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class SerializationTest
{
    private GameClient client;
    
    @Before
    public void setup() {
        client = new GameClient();
    }
    
    @Test
    public void testRealWorldCoordinatesAreSerializable() {
        RealWorldCoordinates rwc = new RealWorldCoordinates(12, 34);
        
        //Serialize Object
        byte[] bytes = client.objectToByteArray(rwc);
        
        //Deserialize Object
        RealWorldCoordinates newRwc = (RealWorldCoordinates) client.getObjectFromBytes(bytes);
        
        //Check that they are equal
        assertThat(newRwc, samePropertyValuesAs(rwc));
    }
    
    @Test
    public void testWaveStateIsSerializable() {
        GameController.WaveState waveState = GameController.WaveState.DefenderBuild;
        
        //Serialize Object
        byte[] bytes = client.objectToByteArray(waveState);
        
        //Deserialize Object
        GameController.WaveState newWavestate = (GameController.WaveState) client.getObjectFromBytes(bytes);
        
        //Check that they are equal
        assertThat(newWavestate, samePropertyValuesAs(waveState));
    }
    
    @Test
    public void testSerializableTowerIsSerializable() {
        SerializableTower tower = new SerializableTower(TowerType.type1, new RealWorldCoordinates(12, 34));
        
        //Serialize Object
        byte[] bytes = client.objectToByteArray(tower);
        
        //Deserialize Object
        SerializableTower newTower = (SerializableTower) client.getObjectFromBytes(bytes);
        
        //Check that they are equal
        assertThat(newTower, samePropertyValuesAs(tower));
    }
    
    @Test
    public void testTimestampedPacketIsSerializable() {
        TimestampedPacket packet = new TimestampedPacket("Test");
        
        //Serialize Object
        byte[] bytes = client.objectToByteArray(packet);
        
        //Deserialize Object
        TimestampedPacket newPacket = (TimestampedPacket) client.getObjectFromBytes(bytes);
        
        //Check that they are equal
        assertThat(newPacket, samePropertyValuesAs(packet));
    }
    
    @Test
    public void testBuildPhaseDataIsSerializable() {
        int[][] units = new int[6][3];
        ArrayList<SerializableTower> tbp = new ArrayList<>();
        BuildPhaseData bpd = new BuildPhaseData(units, tbp, new ArrayList<>(), 0, new ArrayList<>(), "DefenderBuild", 100, 1);
        
        //Serialize Object
        byte[] bytes = client.objectToByteArray(bpd);
        
        //Deserialize Object
        BuildPhaseData newBpd = (BuildPhaseData) client.getObjectFromBytes(bytes);
        
        //Check that they are equal
        assertThat(newBpd, samePropertyValuesAs(bpd));
    }
    
    @After
    public void cleanup() {
    
    }
}
