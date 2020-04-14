package org.lordsofchaos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.lordsofchaos.gameobjects.towers.SerializableTower;

public class BuildPhaseData implements Serializable {

    private static final long serialVersionUID = 1846519348575L;
    private int[][] unitBuildPlan;
    private List<SerializableTower> towerBuildPlan;
    private int defenderUpgradeLevel;
    private int defenderHealth;
    private int attackerUpgradeLevel;
    private List<Integer> pathsUnblockedThisTurn;
    private String currentWave;

    public BuildPhaseData(int[][] unitBuildPlan, List<SerializableTower> towerBuildPlan, int defenderUpgradeLevel,
        List<Integer> pathsUnblockedThisTurn, String currentWave, int defenderHealth,
        int attackerUpgradeLevel) {
        this.unitBuildPlan = unitBuildPlan;
        this.towerBuildPlan = towerBuildPlan;
        this.defenderUpgradeLevel = defenderUpgradeLevel;
        this.defenderHealth = defenderHealth;
        this.pathsUnblockedThisTurn = pathsUnblockedThisTurn;
        this.currentWave = currentWave;
        this.attackerUpgradeLevel = attackerUpgradeLevel;
    }

    public int getAttackerUpgradeLevel() {
        return attackerUpgradeLevel;
    }

    public int[][] getUnitBuildPlan() {
        return unitBuildPlan;
    }

    public List<SerializableTower> getTowerBuildPlan() {
        return towerBuildPlan;
    }

    public List<Integer> getPathsUnblockedThisTurn() {
        return pathsUnblockedThisTurn;
    }

    public int getDefenderUpgradeLevel() {
        return defenderUpgradeLevel;
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

    private void readObject(ObjectInputStream aInputStream)
        throws ClassNotFoundException, IOException {
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.defaultWriteObject();
    }
}
