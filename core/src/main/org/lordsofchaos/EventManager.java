package org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.SerializableTower;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.player.Player;

public class EventManager {

    private static int troopTypes;
    private static int pathCount;

    private static int[][] unitBuildPlan;
    private static List<SerializableTower> towerBuilds;
    private static List<SerializableTower> removedTowers;
    private static int defenderUpgradeLevel;
    private static List<Integer> pathsUnblockedThisTurn;
    private static int attackerUpgradeLevel; // only used for defender


    /**
     * Check if the defender can upgrade, then apply the upgrade and increment the counter, so the
     * attacker knows how many upgrades to apply when they receive the next packet
     */
    public static void defenderUpgrade() {
        if (GameController.canDefenderCanUpgrade()) {
            GameController.defenderUpgrade();
            // if upgrade is successful, need to record this so attacker can upgrade their defender too
            defenderUpgradeLevel = GameController.getDefenderUpgrade();
        }
    }

    /**
     * When the attacker clicks thw upgrade level button (and they have spawned enough troops to
     * earn an upgrade), this function checks they can afford it, then applies it
     */
    public static void attackerUpgrade() {
        if (GameController.canAttackerAffordUpgrade() && GameController.attackerEarnedUpgrade()) {
            GameController.upgradeTroops();
        }
    }

    /**
     * When this client receives a packet from the other client, unpack the information
     *
     * @param bpd the packet sent by the other client
     */
    public static void recieveBuildPhaseData(BuildPhaseData bpd, Player clientPlayerType) {

        // don't apply updates during play phase
        if (GameController.getWaveState() == GameController.WaveState.Play) {
            return;
        }

        if (clientPlayerType == null) {
            return;
        }

        // if the client is defender, only update attacker information
        if (clientPlayerType.equals(GameController.defender)) {
            int previousUpgradeLevel = attackerUpgradeLevel;
            attackerUpgradeLevel = bpd.getAttackerUpgradeLevel();
            unitBuildPlan = bpd.getUnitBuildPlan();
            pathsUnblockedThisTurn = bpd.getPathsUnblockedThisTurn();
            GameController.defenderNetworkUpdates(attackerUpgradeLevel - previousUpgradeLevel);
        }
        // vice versa
        else if (clientPlayerType.equals(GameController.attacker)) {

            int previousUpgradeLevel = defenderUpgradeLevel;
            defenderUpgradeLevel = bpd.getDefenderUpgradeLevel();
            towerBuilds = bpd.getTowerBuildPlan();
            removedTowers = bpd.getRemovedTowers();
            GameController.attackerNetworkUpdates(defenderUpgradeLevel - previousUpgradeLevel);
        }
    }

    /**
     * Called when GameController is initialised, resets all values and makes EventManager ready to
     * start a new game
     *
     * @param givenTroopsTypes how many troop types are in this level
     * @param givenPathCount   how many paths are in this level
     */
    public static void initialise(int givenTroopsTypes, int givenPathCount) {
        troopTypes = givenTroopsTypes;
        pathCount = givenPathCount;
        resetEventManager();
    }

    /**
     * When a newly-placed tower is right clicked, remove it from the game
     *
     * @param tower the tower to remove
     */
    public static void towerRemoved(Tower tower) {
        SerializableTower serTower = findSerializeableTower(tower, towerBuilds);
        if (GameController.removeTower(serTower)) {
            removedTowers.add(serTower);
            towerBuilds.remove(serTower);
        } else {
            System.out.println("Couldn't find tower to remove in gc");
        }
    }

    /**
     * Given a Tower object, search a list of SerializableTower objects and find the one with the
     * same coordinates (if there is one)
     *
     * @param tower              the tower to convert
     * @param serializableTowers the SerializableTowers to search
     */
    public static SerializableTower findSerializeableTower(Tower tower,
        List<SerializableTower> serializableTowers) {
        SerializableTower serTower = null;
        for (SerializableTower serializableTower : serializableTowers) {
            if (serializableTower.getRealWorldCoordinates()
                .equals(tower.getRealWorldCoordinates())) {
                serTower = serializableTower;
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
        return defenderUpgradeLevel;
    }

    public static List<Integer> getPathsUnblockedThisTurn() {
        return pathsUnblockedThisTurn;
    }

    /**
     * When the attacker buys a path, it needs to unblocked in the GameController
     */
    public static void unblockPath(int index) {
        GameController.unblockPath(index, false);
        pathsUnblockedThisTurn.add(index);
    }

    /**
     * When a tower is placed, create a new SerializableTower and pass it to the GameController
     *
     * @param rwc       the position to place the tower
     * @param towerType the type of tower that's been placed
     */
    public static void towerPlaced(TowerType towerType, RealWorldCoordinates rwc) {
        SerializableTower tbp = new SerializableTower(towerType, rwc);
        if (!towerBuilds.contains(tbp) && GameController.verifyTowerPlacement(towerType, rwc)) {
            towerBuilds.add(tbp);
            GameController.createTower(tbp);
        }
    }

    /**
     * Resets all values, called after play phase finishes
     */
    public static void resetEventManager() {
        unitBuildPlan = new int[troopTypes][pathCount];
        towerBuilds = new ArrayList<>();
        removedTowers = new ArrayList<>();
        pathsUnblockedThisTurn = new ArrayList<>();
    }

    /**
     * Whenever a troop is bought, spawned, or cancelled, the buildPlan (which is a frequency
     * matrix) needs to be updated to reflect these changes
     *
     * @param unitType     which kind of unit is in question
     * @param path         which path is the unit assigned to
     * @param change       positive value adds troops to the buildPlan, negative removes
     * @param troopSpawned distinguishes between troops that have been cancelled (and so the player
     *                     should be refunded) and troops that have been spawned. In both cases, the
     *                     troop should be removed from the build plan
     */
    public static void buildPlanChange(int unitType, int path, int change, boolean troopSpawned) {
        if (unitType < 0 || unitType > 2 || path < 0 || path > GameController.getPaths().size()) {
            System.out.println("Invalid buildPlanChange");
            return; // unit or path doesn't exist
        }

        // if path is blocked can't add troop (although this check should be performed before this point)
        if (GameController.getBlockedPaths().contains(path)) {
            System.out.println("Path is blocked");
            return;
        }

        if (change == 1) {// if a troop has been added to the buildPlan
            // check if can afford troop
            if (GameController.canAffordTroop(unitType)) {
                GameController.troopPurchased(unitType);
            } else {
                System.out.println("Can't afford troop type " + unitType + "!");
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