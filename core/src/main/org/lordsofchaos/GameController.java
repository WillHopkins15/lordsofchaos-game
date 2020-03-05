package org.lordsofchaos;

import org.lordsofchaos.coordinatesystems.Coordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.*;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.gameobjects.troops.TroopType1;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;
import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;
import org.lordsofchaos.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameController
{
    public final static float DAMAGEBONUS = 1.5f; // towers do this much times damage against corresponding troop type
    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";
    private static final int scaleFactor = 64;
    public static Attacker attacker = new Attacker(ATTACKERNAME);
    public static Defender defender = new Defender(DEFENDERNAME);
    // this records if the player on the client machine is an attacker or a defender
    public static Player clientPlayerType;
    @SuppressWarnings("unused")
    protected static int wave;
    // list of all troops currently on screen
    protected static List<Troop> troops = new ArrayList<Troop>();
    // list of all towers in matrix
    protected static List<Tower> towers = new ArrayList<Tower>();
    //
    // this list gets iterated through at the end of build phase, each tower gets marked as completed, then the list clears
    protected static List<Tower> towersPlacedThisTurn = new ArrayList<Tower>();
    private static WaveState waveState;
    // timing
    private static float buildTimer = 0;
    private static float buildTimeLimit = 30;
    private static float unitSpawnTimer = 0;
    private static float unitSpawnTimeLimit = 1;
    private static float addMoneyTimer = 0;
    private static float addMoneyTimeLimit = 1;
    // Height and Width of the map
    private static int height;
    private static int width;

    @SuppressWarnings("unused")

    private static int troopsMade = 0;
    private static int upgradeNo = 0;
    private static int healthUpgrade = 0;
    private static int speedUpgrade = 0;
    private static int damageUpgrade = 0;


    // A list containing different lists that are have the co-ordinates of a paths
    private static List<List<Path>> paths = new ArrayList<List<Path>>();
    private static List<Coordinates> obstacles = new ArrayList<Coordinates>();
    // The 2 dimensional array to represent the map
    private static MatrixObject[][] map;
    
    public static float getBuildPhaseTimer() {
        return buildTimer;
    }
    
    public static WaveState getWaveState() {
        return waveState;
    }
    
    public static List<Tower> getTowers() {
        return towers;
    }
    
    public static MatrixObject[][] getMap() {
        return map;
    }
    
    public static List<Troop> getTroops() {
        return troops;
    }
    
    public static int getScaleFactor() {
        return scaleFactor;
    }
    
    public static List<List<Path>> getPaths() {
        return paths;
    }
    
    public static void setPlayerType(Boolean type) {
        if (type) {
            clientPlayerType = defender;
        } else {
            clientPlayerType = attacker;
        }
    }
    
    public static void initialise() {
        buildTimer = 0;
        unitSpawnTimer = 0;
        addMoneyTimer = 0;
        waveState = WaveState.DefenderBuild;
        height = 20;
        width = 20;
        wave = 1;
        paths = MapGenerator.generatePaths();
        obstacles = MapGenerator.getObstacles();
        map = MapGenerator.generateMap(width, height, paths, obstacles);
        EventManager.initialise(6, getPaths().size());
        //debugVisualiseMap();
    }
    
    public static BuildPhaseData getGameState() {
        // send towerBuilds and unitBuildPlan over network
        BuildPhaseData bpd = new BuildPhaseData(EventManager.getUnitBuildPlan(), EventManager.getTowerBuilds());
        //System.out.println("Get Game State: " + bpd.toString());
        return bpd;
        // then clear data ready for next turn
    }
    
    public static void setGameState(BuildPhaseData bpd) {
        EventManager.recieveBuildPhaseData(bpd);

        if (clientPlayerType == null)
            return;

        // place towers that were placed by defender in the build phase
        if (clientPlayerType.equals(attacker)) {
            for (int i = 0; i < EventManager.getTowerBuilds().size(); i++) {
                boolean alreadyExists = false;
                // check if tower has not already benn added
                for (int j = 0; j < towersPlacedThisTurn.size(); j++)
                {
                    if (towersPlacedThisTurn.get(j).getRealWorldCoordinates().equals(EventManager.getTowerBuilds().get(i).getRealWorldCoordinates()))
                    {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists)
                    towersPlacedThisTurn.add(createTower(EventManager.getTowerBuilds().get(i)));
            }
        }
    }
    
    private static void resetBuildTimer() {
        buildTimer = 0;
    }
    
    private static void resetUnitSpawnTimer() {
        unitSpawnTimer = 0;
    }
    
    private static void resetAddMoneyTimer() {
        addMoneyTimer = 0;
    }
    
    public static void endPhase() {
        Game.newTurn();
        if (waveState == WaveState.DefenderBuild) {
            waveState = WaveState.AttackerBuild;
            
            // mark all placed towers as complete
            for (int i = 0; i < towersPlacedThisTurn.size(); i++) {
                towersPlacedThisTurn.get(i).setIsCompleted();
            }
            towersPlacedThisTurn.clear();
            
            System.out.println("Attacker build phase begins");
            
            resetBuildTimer();
        } else if (waveState == WaveState.AttackerBuild) {
            waveState = WaveState.Play;
            System.out.println("Play begins");
            wave++;
            resetBuildTimer();
        } else {
            waveState = WaveState.DefenderBuild;
            
            System.out.println("Defender build phase begins");
            
            // reset all tower cooldowns
            if (!GameController.towers.isEmpty()) {
                for (int j = 0; j < GameController.towers.size(); j++) {
                    GameController.towers.get(j).resetTimer();
                }
            }
            
            // make sure to reset all tower build plans and unit build plans
            EventManager.resetEventManager();
            resetAddMoneyTimer();
            resetUnitSpawnTimer();
        }
    }
    
    // called by renderer every frame/ whatever
    public static void update(float deltaTime) {
        if (waveState == WaveState.DefenderBuild) {
            buildTimer += deltaTime;
            // if time elapsed, change state to attackerBuild
            if (buildTimer > buildTimeLimit) {
                endPhase();
            }
        } else if (waveState == WaveState.AttackerBuild) {
            buildTimer += deltaTime;
            // if time elapsed, plus wave and change state to play
            if (buildTimer > buildTimeLimit) {
                endPhase();
            }
        } else {
            // if defender health reaches zero, game over
            if (defender.getHealth() <= 0) {
                System.out.println("Defender loses");
            }
            // if no troops on screen and none in the spawn queue
            else if (GameController.troops.isEmpty() && unitBuildPlanEmpty()) {
                endPhase();
                addMoney();
                
            } else {
                shootTroops(deltaTime);
                moveTroops(deltaTime);
                spawnTroop(deltaTime);
            }
        }
    }
    
    private static void addMoney() {
        attacker.addMoney();
        defender.addMoney();
    }
    
    private static void addMoney(float deltaTime) {
        addMoneyTimer += deltaTime;
        if (addMoneyTimer > addMoneyTimeLimit) {
            attacker.addMoney();
            defender.addMoney();
            resetAddMoneyTimer();
        }
    }
    
    private static Boolean unitBuildPlanEmpty() {
        int paths = EventManager.getUnitBuildPlan()[0].length;
        int types = EventManager.getUnitBuildPlan().length;
        
        for (int path = 0; path < paths; path++) {
            for (int type = 0; type < types; type++) {
                if (EventManager.getUnitBuildPlan()[type][path] != 0) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static void spawnTroop(float deltaTime) {
        unitSpawnTimer += deltaTime;
        if (unitSpawnTimer > unitSpawnTimeLimit) {
            // loop through each path and spawn a troop into each
            for (int path = 0; path < getPaths().size(); path++) {
                // using only troop type 0 for prototype
                if (EventManager.getUnitBuildPlan()[0][path] > 0) {
                    //calls upgrade troop function
                    upgradeTroops();
                    //creates new troop
                    Troop newTroop = new TroopType1(getPaths().get(path));
                    //checks if upgrades have happened
                    //if so newTroop is upgraded
                    if (upgradeNo != 0) {
                        newTroop.setCurrentHealth(newTroop.getCurrentHealth() + healthUpgrade);
                        newTroop.setMovementSpeed(newTroop.getMovementSpeed() + speedUpgrade);
                        newTroop.setDamage(newTroop.getDamage() + damageUpgrade);
                    }
                    // add troop to on screen troops
                    GameController.troops.add(newTroop);
                    //updates number of troops made
                    troopsMade++;
                    // remove from build plan
                    EventManager.buildPlanChange(0, path, -1, true);
                }
            }
            // spawn troop into each path
            resetUnitSpawnTimer();
        }
    }
    
    public static void moveTroops(float deltaTime) {
        int size = GameController.troops.size();
        
        // any troops that reach the end will be stored here and removed at the end
        List<Troop> troopsToRemove = new ArrayList<Troop>();
        
        // move troops
        for (int i = 0; i < size; i++) {
            (GameController.troops.get(i)).move(deltaTime);
            
            if (GameController.troops.get(i).getAtEnd()) {
                troopsToRemove.add((GameController.troops.get(i)));
            }
        }
        
        // remove any troops that have reached the end
        for (int i = 0; i < troopsToRemove.size(); i++) {
            troopReachesDefender(troopsToRemove.get(i));
        }
    }
    
    private static void troopReachesDefender(Troop troop) {
        defender.takeDamage(troop.getDamage());
        troopDies(troop);
    }
    
    public static void shootTroops(float deltaTime) {
        if (!GameController.towers.isEmpty()) {
            for (int j = 0; j < GameController.towers.size(); j++) {
                GameController.towers.get(j).shoot(deltaTime);
            }
        }
    }
    
    private static void debugVisualiseMap() {
        for (int y = height - 1; y > -1; y--) {
            for (int x = 0; x < width; x++) {
                if (map[y][x].getClass() == Tile.class) {
                    Tile t = (Tile) map[y][x];
                    if (t.getIsBuildable()) {
                        if (t.getTower() != null) {
                            System.out.println("T ");
                        } else {
                            System.out.print("- ");
                        }
                    } else {
                        System.out.print("X ");
                    }
                } else if (map[y][x].getClass() == Path.class) {
                    System.out.print("@ ");
                } else {
                    System.out.print("!");
                }
            }
        }
    }
    
    private static void troopDies(Troop troop) {
        if (troops.contains(troop)) {
            troops.remove(troop);
            
            // look through the path this troop is on and remove it from the Path it's
            // contained in
            for (int i = 0; i < troop.getPath().size(); i++) {
                Path path = troop.getPath().get(i);
                for (int j = 0; j < troop.getPath().get(i).getTroops().size(); j++) {
                    if (troop.equals(path.getTroops().get(j))) {
                        path.removeTroop(troop);
                        break;
                    }
                }
            }
        }
    }
    
    public static MatrixObject getMatrixObject(int y, int x) {
        return map[y][x];
    }
    
    public static void shootTroop(Tower tower, Troop troop) {
        // will have to call sound and graphics for shooting at troop
        int temp;
        temp = troop.getCurrentHealth() - tower.getDamage();
        troop.setCurrentHealth(temp);
        
        if (troop.getCurrentHealth() <= 0) {
            troopDies(troop);
        }
    }

    public static Tower createTower(SerializableTower tbp) {
        Tower tower = null;
        
        // convert realWorldCoords to matrix
        MatrixCoordinates mc = new MatrixCoordinates(tbp.getRealWorldCoordinates());
        
        Tile tile = (Tile) map[mc.getY()][mc.getX()];
        
        if (tbp.getTowerType() == TowerType.type1) {
            tower = new TowerType1(tbp.getRealWorldCoordinates());
        }
        else if (tbp.getTowerType() == TowerType.type2) {
            tower = new TowerType2(tbp.getRealWorldCoordinates());
        }
        else if (tbp.getTowerType() == TowerType.type3) {
            tower = new TowerType3(tbp.getRealWorldCoordinates());
        }
        
        towers.add(tower);
        towersPlacedThisTurn.add(tower);
        tile.setTower(tower);
        
        // we have already checked if the defender can afford this tower, so now take away money
        defender.addMoney(-tower.getCost());
        return tower;
    }

    public static void removeTower(Tower tower) {
        towers.remove(tower);
        towersPlacedThisTurn.remove(tower);
        Tile tile = (Tile) map[tower.getRealWorldCoordinates().getY()][tower.getRealWorldCoordinates().getX()];
        tile.setTower(null);
        defender.addMoney(tower.getCost());
        System.out.println("Tower removed at " + tower.getRealWorldCoordinates().getY() + "," + tower.getRealWorldCoordinates().getX());
    }
    
    public static boolean inBounds(MatrixCoordinates mc) {
        return mc.getX() >= 0 && mc.getY() >= 0 && mc.getX() < width && mc.getY() < height;
    }
    
    public static boolean inBounds(int y, int x) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }
    
    // want to find the cost of a tower before it has been placed
    private static int getTowerTypeCost(TowerType towerType) {
        if (towerType == TowerType.type1) {
            return 10;
        } else {
            return 0;
        }
    }
    
    private static int getTroopTypeCost(int troopType) {
        if (troopType == 0) {
            return 10;
        } else return 0;
        // add elses for other troops here
    }
    
    // used by EventManager
    public static boolean canAffordTroop(int troopType) {
        return attacker.getCurrentMoney() >= getTroopTypeCost(troopType);
    }
    
    public static boolean canAffordTower(TowerType towerType) {
        return clientPlayerType.getCurrentMoney() >= getTowerTypeCost(towerType);
    }
    
    // once a purchase has been verified and added to event manager, finally need to take money from attacker
    public static void troopPurchased(int troopType) {
        attacker.addMoney(-getTroopTypeCost(troopType));
    }
    
    public static void troopCancelled(int troopType, int path) {
        // check if build plan is empty at that place, if so, don't give a refund
        if (EventManager.getUnitBuildPlan()[troopType][path] > 0) {
            attacker.addMoney(getTroopTypeCost(troopType));
        }
    }
    
    public static boolean verifyTowerPlacement(TowerType towerType, RealWorldCoordinates rwc) {
        // convert realWorldCoords to matrix
        MatrixCoordinates mc = new MatrixCoordinates(rwc);
        
        // check if given mc is actually within the bounds of the matrix
        if (!inBounds(mc)) {
            return false;
        }
        
        if (!canAffordTower(towerType)) {
            System.out.println("Can't afford tower type " + towerType + "!");
            return false;
        }
        
        // check if this matrix position is legal
        MatrixObject mo = map[mc.getY()][mc.getX()];
        if (mo.getClass() == Path.class) {
            return false; // cannot place towers on path
        } else if (mo instanceof Tile) {
            Tile tile = (Tile) mo;
            return tile.getTower() == null && tile.getIsBuildable(); // else it is a tile, but a tower exists here already
        }
        return true;
    }

    public static void upgradeTroops(){
        if ((troopsMade % 25) == 0) {
            upgradeNo = upgradeNo + 1;
            int type = upgradeNo % 3;

            switch (type) {
                //upgrades health
                case 1:
                    healthUpgrade = healthUpgrade + 5;
                    break;
                //upgrades speed
                case 2:
                    speedUpgrade = speedUpgrade + 2;
                    break;
                //upgrades damage
                case 3:
                    damageUpgrade = damageUpgrade + 3;
                    break;
            }

            if (!troops.isEmpty()) {
                for (int i = 0; i < troops.size(); i++) {
                    switch (type) {
                        //upgrades health
                        case 1:
                            troops.get(i).setCurrentHealth(troops.get(i).getCurrentHealth() + healthUpgrade);
                            break;
                        //upgrades speed
                        case 2:
                            troops.get(i).setMovementSpeed(troops.get(i).getMovementSpeed() + speedUpgrade);
                            break;
                        //upgrades damage
                        case 3:
                            troops.get(i).setDamage(troops.get(i).getDamage() + damageUpgrade);
                            break;
                    }
                }
            }
        }
    }

    
    public enum WaveState
    {
        DefenderBuild, AttackerBuild, Play
    }
}