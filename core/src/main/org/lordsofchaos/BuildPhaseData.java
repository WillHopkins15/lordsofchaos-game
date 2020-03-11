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
    private List<Integer> pathsUnblockedThisTurn;
    
    public BuildPhaseData(int[][] unitBuildPlan, List<SerializableTower> towerBuildPlan, int defenderUpgradesThisTurn,
                          List<Integer> pathsUnblockedThisTurn) {
        this.unitBuildPlan = unitBuildPlan;
        this.towerBuildPlan = towerBuildPlan;
        this.defenderUpgradesThisTurn = defenderUpgradesThisTurn;
        this.pathsUnblockedThisTurn = pathsUnblockedThisTurn;
    }
    
    public int[][] getUnitBuildPlan() {
        return unitBuildPlan;
    }
    
    public List<SerializableTower> getTowerBuildPlan() {
        return towerBuildPlan;
    }

    public List<Integer> getPathsUnblockedThisTurn() { return pathsUnblockedThisTurn; }

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
