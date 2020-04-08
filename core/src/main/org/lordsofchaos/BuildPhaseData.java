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
    private static final long serialVersionUID = 1846519348571L;
    private int[][] unitBuildPlan;
    private List<SerializableTower> towerBuildPlan;
    private List<SerializableTower> removedTowers;
    private int defenderUpgradesThisTurn;
    private int defenderHealth;
    private int attackerUpgradeLevel;
    private List<Integer> pathsUnblockedThisTurn;
    private String currentWave;
    
    public BuildPhaseData(int[][] unitBuildPlan, List<SerializableTower> towerBuildPlan, List<SerializableTower> removedTowers, int defenderUpgradesThisTurn,
                          List<Integer> pathsUnblockedThisTurn, String currentWave, int defenderHealth, int attackerUpgradeLevel) {
        this.unitBuildPlan = unitBuildPlan;
        this.towerBuildPlan = towerBuildPlan;
        this.defenderUpgradesThisTurn = defenderUpgradesThisTurn;
        this.defenderHealth = defenderHealth;
        this.pathsUnblockedThisTurn = pathsUnblockedThisTurn;
        this.removedTowers = removedTowers;
        this.currentWave = currentWave;
        this.attackerUpgradeLevel = attackerUpgradeLevel;
    }

    public int getAttackerUpgradeLevel() {return attackerUpgradeLevel;}

    public int[][] getUnitBuildPlan() {
        return unitBuildPlan;
    }
    
    public List<SerializableTower> getTowerBuildPlan() {
        return towerBuildPlan;
    }
    
    public List<SerializableTower> getRemovedTowers() {
        return removedTowers;
    }
    
    public List<Integer> getPathsUnblockedThisTurn() {
        return pathsUnblockedThisTurn;
    }
    
    public int getDefenderUpgradesThisTurn() {
        return defenderUpgradesThisTurn;
    }
    
    public int getDefenderHealth() {
        return defenderHealth;
    }
    
    public String getCurrentWave() {
        return currentWave;
    }
    
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
