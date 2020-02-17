package org.lordsofchaos;

import org.lordsofchaos.EventManager.TowerBuild;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.towers.TowerType1;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;
import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;

import java.util.ArrayList;
import java.util.List;


public class GameController
{
    
    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";
    public static Attacker attacker = new Attacker(ATTACKERNAME);
    public static Defender defender = new Defender(DEFENDERNAME);
    @SuppressWarnings("unused")
    protected static int wave = 1;
    protected static List<Troop> troops = new ArrayList<Troop>();
    protected static List<Tower> towers = new ArrayList<Tower>();
    private static WaveState waveState;
    private static int scaleFactor = 100;
    //Height and Width of the map
    private static int height;
    private static int width;
    // during defender build phase, when player places a tower, add a build plan here
    private static List<TowerBuild> towerBuilds = new ArrayList<TowerBuild>();
    //A list containing different lists that are have the co-ordinates of a paths
    private static List<List<Path>> paths = new ArrayList<List<Path>>();
    //The 2 dimensional array to represent the map
    private static MatrixObject[][] map;
    
    public static List<Troop> getTroops() {
        return troops;
    }
    
    public static int getScaleFactor() {
        return scaleFactor;
    }
    
    public static List<List<Path>> getPaths() {
        return paths;
    }
    
    public static void initialise() {
        waveState = WaveState.DefenderBuild;
        height = 10;
        width = 10;
        wave = 0;
        paths = MapGenerator.generatePaths();
        map = MapGenerator.generateMap(width, height, paths);
        debugVisualiseMap();
    }
    
    public static void sendData() {
        // send towerBuilds and unitBuildPlan over network
        BuildPhaseData bpd = new BuildPhaseData(EventManager.getUnitBuildPlan(),
                towerBuilds);
        
        // then clear data ready for next turn
        EventManager.resetBuildPlan();
        towerBuilds.clear();
    }
    
    // called by renderer every frame/ whatever
    public static void update(float deltaTime) {
        if (waveState == WaveState.DefenderBuild) {
            
            // if time elapsed, change state to attackerBuild
            // waveState = WaveState.AttackerBuild;
        } else if (waveState == WaveState.AttackerBuild) {
            
            // if time elapsed, plus wave and change state to play
        } else {
            shootTroops();
            moveTroops();
            spawnTroop();
            //plusWave();
            attacker.addMoney();
            defender.addMoney();
            // add money
            // spawn in troops
        }
        
    }
    
    private static void spawnTroop() {
    
    }
    
    public static void moveTroops() {
        int size = GameController.troops.size();
        
        for (int i = 0; i < size; i++) {
            (GameController.troops.get(i)).move();
            
            if (GameController.troops.get(i).getAtEnd()) {
                GameController.troops.remove((GameController.troops.get(i)));
            }
        }
    }
    
    public static void shootTroops() {
        if (!GameController.towers.isEmpty()) {
            for (int j = 0; j < GameController.towers.size(); j++) {
                GameController.towers.get(j).shoot();
            }
        }
    }
    
    private static void debugVisualiseMap() {
        for (int x = 0; x < width; x++) {
            System.out.println();
            for (int y = 0; y < height; y++) {
                if (map[y][x].getClass() == Tile.class) {
                    System.out.print("@");
                } else if (map[y][x].getClass() == Path.class) {
                    System.out.print("P");
                } else {
                    System.out.print("!");
                }
            }
        }
    }
    
    public static MatrixObject getMatrixObject(int y, int x) {
        return map[y][x];
    }
    
    public static void shootTroop(Tower tower, Troop troop) {
        //will have to call sound and graphics for shooting at troop
        int temp;
        temp = troop.getCurrentHealth() - tower.getDamage();
        troop.setCurrentHealth(temp);
        
        if (troop.getCurrentHealth() <= 0) {
            //sound and graphic to remove the troop;
        }
    }
    
    // called when user attempts to place a tower
    // - could be an illegal place, has yet to be verified
    public static void towerPlaced(TowerBuild tbp) {
        if (!verifyTowerPlacement(tbp.getRealWorldCoordinates())) {
            return;
        }
        // convert realWorldCoords to matrix
        MatrixCoordinates mc = new MatrixCoordinates(tbp.getRealWorldCoordinates());
        
        Tower tower = createTower(tbp);
        
        Tile tile = (Tile) map[mc.getY()][mc.getX()];
        tile.setTower(tower);
        
        towerBuilds.add(tbp);
    }
    
    private static Tower createTower(TowerBuild tbp) {
        Tower tower = null;
        if (tbp.getTowerType() == TowerType.type1) {
            tower = new TowerType1(tbp.getRealWorldCoordinates());
        }
        // other if's to be added when new towers are added
        return tower;
    }
    
    public static boolean verifyTowerPlacement(RealWorldCoordinates rwc) {
        // convert realWorldCoords to matrix
        MatrixCoordinates mc = new MatrixCoordinates(rwc);
        // check if this matrix position is legal
        MatrixObject mo = map[mc.getY()][mc.getX()];
        if (mo.getClass() == Path.class) {
            return false; // cannot place towers on path
        } else if ((mo.getClass() == Tile.class)
                && (((Tile) mo).getTower()) != null) {
            return false; // else it is a tile, but a tower exists here already
        }
        return true;
    }
    
    enum WaveState
    {
        DefenderBuild, AttackerBuild, Play
    }
}