package org.lordsofchaos;

import org.lordsofchaos.gameobjects.towers.SerializableTower;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BuildPhaseData implements Serializable
{
    private static final long serialVersionUID = 5084L;
    private int[][] unitBuildPlan;
    private List<SerializableTower> towerBuildPlan;
    private int defenderUpgradesThisTurn;
    
    public BuildPhaseData(int[][] unitBuildPlan, List<SerializableTower> towerBuildPlan, int defenderUpgradesThisTurn) {
        this.unitBuildPlan = unitBuildPlan;
        this.towerBuildPlan = towerBuildPlan;
        this.defenderUpgradesThisTurn = defenderUpgradesThisTurn;
    }
    
    public int[][] getUnitBuildPlan() {
        return unitBuildPlan;
    }
    
    public List<SerializableTower> getTowerBuildPlan() {
        return towerBuildPlan;
    }

    public int getDefenderUpgradesThisTurn() { return defenderUpgradesThisTurn; }
    
    public String toString() {
        String units = Arrays.deepToString(unitBuildPlan);
        String towers = towerBuildPlan.toString();
        return "Units: " + units + " Towers: " + towers;
    }
    
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        aInputStream.defaultReadObject();
    }
    
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.defaultWriteObject();
    }
}
