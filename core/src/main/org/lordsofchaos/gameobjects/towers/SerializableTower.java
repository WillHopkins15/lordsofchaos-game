package org.lordsofchaos.gameobjects.towers;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;

import java.io.Serializable;

public class SerializableTower implements Serializable
{
    // SerializableTower tells you what sort of tower has been placed and where,
    // GameController then uses this to create instances of towers
    // GameController contains a list of SerializableTowers which will be sent over the
    // network
    
    private RealWorldCoordinates rwc;
    private TowerType towerType;
    
    public SerializableTower(TowerType towerType, RealWorldCoordinates rwc) {
        this.rwc = rwc;
        this.towerType = towerType;
    }
    
    public RealWorldCoordinates getRealWorldCoordinates() {
        return rwc;
    }
    
    public TowerType getTowerType() {
        return towerType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SerializableTower) {
            SerializableTower towerBuild = (SerializableTower) obj;
            MatrixCoordinates thisMC = new MatrixCoordinates(getRealWorldCoordinates());
            MatrixCoordinates otherMC = new MatrixCoordinates(towerBuild.getRealWorldCoordinates());
            return thisMC.equals(otherMC);
        }
        return false;
    }
    
    public String toString() {
        String tower = this.towerType.getSpriteName();
        String coords = rwc.toString();
        return "Type: " + tower + " rwc: " + coords;
    }
}
