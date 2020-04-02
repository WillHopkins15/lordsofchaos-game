package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.SerializableTower;
import org.lordsofchaos.gameobjects.towers.Tower;

import java.util.ArrayList;
import java.util.List;

public class EventManager
{
    private static int troopTypes;
    private static int pathCount;
    
    private static int[][] unitBuildPlan;
    private static List<SerializableTower> towerBuilds;
    private static List<SerializableTower> removedTowers;
    private static int defenderUpgradesThisTurn;
    private static List<Integer> pathsUnblockedThisTurn;
    
    public static void defenderUpgrade() {
        if (GameController.canDefenderCanUpgrade()) {
            GameController.defenderUpgrade();
            // if upgrade is successful, need to record this so attacker can upgrade their defender too
            defenderUpgradesThisTurn++;
        }
    }
    
    public static void recieveBuildPhaseData(BuildPhaseData bpd) {
        unitBuildPlan = bpd.getUnitBuildPlan();
        towerBuilds = bpd.getTowerBuildPlan();
        defenderUpgradesThisTurn = bpd.getDefenderUpgradesThisTurn();
        pathsUnblockedThisTurn = bpd.getPathsUnblockedThisTurn();
        removedTowers = bpd.getRemovedTowers();
    }
    
    public static void initialise(int givenTroopsTypes, int givenPathCount) {
        troopTypes = givenTroopsTypes;
        pathCount = givenPathCount;
        resetEventManager();
    }
    
    // should be called when right clicking on tower
    public static void towerRemoved(Tower tower) {
        SerializableTower serTower = findSerializeableTower(tower, towerBuilds);
        if (GameController.removeTower(serTower)) {
            removedTowers.add(serTower);
            towerBuilds.remove(serTower);
        } else {
            System.out.println("Couldn't find tower to remove in gc");
        }
    }
    
    public static SerializableTower findSerializeableTower(Tower tower, List<SerializableTower> serializableTowers) {
        SerializableTower serTower = null;
        for (int i = 0; i < serializableTowers.size(); i++) {
            if (serializableTowers.get(i).getRealWorldCoordinates().equals(tower.getRealWorldCoordinates())) {
                serTower = serializableTowers.get(i);
                break;
            }
        }
        return serTower;
    }
    
    public static int[][] getUnitBuildPlan() {
        return unitBuildPlan;
    }
    
    public static List<SerializableTower> getTowerBuilds() {
        return towerBuilds;
    }
    
    public static List<SerializableTower> getRemovedTowers() {
        return removedTowers;
    }
    
        public static int getDefenderUpgradesThisTurn() {
        return defenderUpgradesThisTurn;
    }
    
    public static List<Integer> getPathsUnblockedThisTurn() {
        return pathsUnblockedThisTurn;
    }
    
    public static void unblockPath(int index) {
        if (GameController.canAttackerUnblockPath()) {
            GameController.unblockPath(index);
            pathsUnblockedThisTurn.add(index);
        }
    }
    
    public static void towerPlaced(TowerType towerType, RealWorldCoordinates rwc) {
        SerializableTower tbp = new SerializableTower(towerType, rwc);
        if (!towerBuilds.contains(tbp) && GameController.verifyTowerPlacement(towerType, rwc)) {
            towerBuilds.add(tbp);
            GameController.createTower(tbp);
        }
    }
    
    public static void resetEventManager() {
        unitBuildPlan = new int[troopTypes][pathCount];
        towerBuilds = new ArrayList<>();
        removedTowers = new ArrayList<>();
        defenderUpgradesThisTurn = 0;
        pathsUnblockedThisTurn = new ArrayList<>();
    }
    
    public static void buildPlanChange(int unitType, int path, int change, boolean troopSpawned) {
        if (unitType < 0 || unitType > 2 || path < 0 || path > GameController.getPaths().size()) {
            System.out.println("Invalid buildPlanChange");
            return; // unit or path doesn't exist
        }
        
        // if path is blocked can't add troop (although this check should be performed before this point)
        if (GameController.getBlockedPaths().contains(new Integer(path))) {
            System.out.println("Path is blocked");
            return;
        }
        
        if (change == 1) {// if a troop has been added to the buildPlan
            // check if can afford troop
            if (GameController.canAffordTroop(unitType)) {
                GameController.troopPurchased(unitType);
            } else {
                System.out.print("Can't afford troop type " + unitType + "!");
                return;
            }
        } else if (change == -1) {
            // attacker should receive a refund if a troop has been cancelled
            if (!troopSpawned) {
                GameController.troopCancelled(unitType, path);
            }
        }
        
        // get the number of units currently in the matrix position
        // add the change to this position, clamping value so it can't be negative
        int current = unitBuildPlan[unitType][path];
        unitBuildPlan[unitType][path] = clamp(current, change, 0);
    }
    
    private static int clamp(int value, int change, int min) {
        int newVal = value + change;
        return Math.max(newVal, min);
    }
}