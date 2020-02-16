package org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;

import org.lordsofchaos.EventManager.TowerBuild;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.gameobjects.GameObject;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.towers.TowerType1;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.gameobjects.troops.TroopType1;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;
import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;
import org.lordsofchaos.player.Player;

import com.badlogic.gdx.scenes.scene2d.Event;


public class GameController {

    enum WaveState{
        DefenderBuild, AttackerBuild, Play
    }

    private static WaveState waveState;
    
    // timing 
    private static float buildTimer = 0; 
    private static float buildTimeLimit = 30;
    
    private static float unitSpawnTimer = 0;
    private static float unitSpawnTimeLimit = 1;
    
    private static float addMoneyTimer = 0;
    private static float addMoneyTimeLimit = 1;
    //

    private static int scaleFactor = 100;
    //Height and Width of the map
    private static int height;
    private static int width;
    @SuppressWarnings("unused")
	protected static int wave = 1;
    
    // list of all troops currently on screen
    protected static List<Troop> troops = new ArrayList<Troop>();
    protected static List<Tower> towers = new ArrayList<Tower>();

    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";

    public static Attacker attacker = new Attacker(ATTACKERNAME);
    public static Defender defender = new Defender(DEFENDERNAME);
    
    // this records if the player on the client machine is an attacker or a defender
    public static Player clientPlayerType;
    
    //A list containing different lists that are have the co-ordinates of a paths
    private static List<List<Path>> paths = new ArrayList<List<Path>>();

    //The 2 dimensional array to represent the map
    private static MatrixObject[][] map;
    
    public static MatrixObject[][] getMap()
    {
    	return map;
    }
    
    public static List<Troop> getTroops()
    {
        return troops;
    }
    
    public static int getScaleFactor()
    {
    	return scaleFactor;
    }
    
    public static List<List<Path>> getPaths()
    {
    	return paths;
    }

    public static void setPlayerType(Boolean type)
    {
    	if (type)
    	{
        	clientPlayerType = defender;
    	}
    	else
    	{
        	clientPlayerType = attacker;
    	}
    }
    
    public static void initialise()
    {
    	buildTimer = 0;
    	unitSpawnTimer = 0;
    	addMoneyTimer = 0;
        waveState = WaveState.DefenderBuild;
    	height = 20;
    	width = 20;
    	wave = 0;
        paths = MapGenerator.generatePaths();
        map = MapGenerator.generateMap(width, height, paths);
        EventManager.initialise(6, getPaths().size());
        debugVisualiseMap();
    }
    
    public static void sendData()
    {
    	// send towerBuilds and unitBuildPlan over network
    	BuildPhaseData bpd = new BuildPhaseData(EventManager.getUnitBuildPlan(), 
    			EventManager.getTowerBuilds());
    	
    	// then clear data ready for next turn
    	
    	// server needs to send BuildPhaseData to EventManager like this:
    	//EventManager.recieveBuildPhaseData(bpd);
    }
    
    private static void resetBuildTimer()
    {
    	buildTimer = 0;
    }
    
    private static void resetUnitSpawnTimer()
    {
    	unitSpawnTimer = 0;
    }
    
    private static void resetAddMoneyTimer()
    {
    	addMoneyTimer = 0;
    }

    // called by renderer every frame/ whatever
    public static void update(float deltaTime)
    {
        if (waveState == WaveState.DefenderBuild)
        {
        	buildTimer+= deltaTime;
            // if time elapsed, change state to attackerBuild
            // waveState = WaveState.AttackerBuild;
        	if (buildTimer > buildTimeLimit)
        	{
        		waveState = WaveState.AttackerBuild;
        		
        		// create all towers 
        		for (int i = 0; i < EventManager.getTowerBuilds().size(); i++)
        		{
        			createTower(EventManager.getTowerBuilds().get(i));
        		}
        		
        		System.out.println("Attacker build phase begins");
        		resetBuildTimer();
        	}
        }
        else if (waveState == WaveState.AttackerBuild)
        {
        	buildTimer+= deltaTime;
            // if time elapsed, plus wave and change state to play
         	if (buildTimer > buildTimeLimit)
        	{
        		waveState = WaveState.Play;
        		System.out.println("Play begins");
        		wave ++;
        		resetBuildTimer();
        	}
        }
        else {
        	// if defender health reaches zero, game over
        	if (defender.getHealth() <= 0)
        	{
        		System.out.println("Defender loses");
        	}
        	// if no troops on screen and none in the spawn queue
        	else if (GameController.troops.isEmpty() && unitBuildPlanEmpty()) 
			{      
        		waveState = WaveState.DefenderBuild;
        		
                 // reset all tower cooldowns
                 if (!GameController.towers.isEmpty()) {
                     for (int j = 0; j < GameController.towers.size(); j++){
                         GameController.towers.get(j).resetTimer();
                     }
                 }
                 
                // make sure to reset all tower build plans and unit build plans
             	EventManager.resetEventManager();
             	
                resetAddMoneyTimer();
                resetUnitSpawnTimer();
            }     
        	else
        	{
        		shootTroops(deltaTime);
        		moveTroops(deltaTime);
        		spawnTroop(deltaTime);
        		addMoney(deltaTime);
        	}
        }
    }
    
    private static void addMoney(float deltaTime)
    {
    	addMoneyTimer += deltaTime;
    	if (addMoneyTimer > addMoneyTimeLimit)
    	{
    		attacker.addMoney();
            defender.addMoney();
            resetAddMoneyTimer();
    	}
    }
    
    private static Boolean unitBuildPlanEmpty()
    {
    	int paths = EventManager.getUnitBuildPlan()[0].length;
    	int types = EventManager.getUnitBuildPlan().length;
    	
    	for (int path = 0; path < paths; path++)
    	{
    		for (int type = 0; type < types; type++)
    		{    		
    			if (EventManager.getUnitBuildPlan()[type][path] != 0)
    			{
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }

    private static void spawnTroop(float deltaTime)
    {
    	unitSpawnTimer += deltaTime;
    	if (unitSpawnTimer > unitSpawnTimeLimit)
    	{
    		// loop through each path and spawn a troop into each
    		for (int path = 0; path < getPaths().size(); path++)
    		{
    			// using only troop type 0 for prototype
    			if (EventManager.getUnitBuildPlan()[0][path] > 0)
    			{
    				// add troop to on screen troops
    				GameController.troops.add(new TroopType1(getPaths().get(path)));
    				
    				// remove from build plan
    				EventManager.buildPlanChange(0, path, -1);
    			}
    		}
    		// spawn troop into each path
    		resetUnitSpawnTimer();
    	}
    }

    public static void moveTroops(float deltaTime) {
        int size = GameController.troops.size();

        for (int i = 0; i < size; i++) {
            (GameController.troops.get(i)).move();

            if (GameController.troops.get(i).getAtEnd()) {
                GameController.troops.remove((GameController.troops.get(i)));
            }
        }
    }

    public static void shootTroops(float deltaTime)  {
        if (!GameController.towers.isEmpty()) {
            for (int j = 0; j < GameController.towers.size(); j++){
                GameController.towers.get(j).shoot(deltaTime);
            }
        }
    }
    
    private static void debugVisualiseMap()
    {
        for (int y = height-1; y > -1; y--)
        {
            System.out.println();
            for (int x = 0; x < width; x++)
            {
                if (map[y][x].getClass() == Tile.class)
                {
                    System.out.print("- ");
                }
                else if (map[y][x].getClass() == Path.class)
                {
                    System.out.print("@ ");
                }
                else
                {
                    System.out.print("!");
                }
            }   
        }
    }
    
    private static void troopDies(Troop troop)
    {
    	//sound and graphic to remove the troop;
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
        	troopDies(troop);
        }
    }
    
    private static Tower createTower(TowerBuild tbp)
    {
    	Tower tower = null;
    	
    	// convert realWorldCoords to matrix
    	MatrixCoordinates mc = new MatrixCoordinates(tbp.getRealWorldCoordinates());
    	
    	Tile tile = (Tile)map[mc.getY()][mc.getX()];
    	
    	if (tbp.getTowerType() == TowerType.type1)
    	{
    		tower = new TowerType1(tbp.getRealWorldCoordinates());
    	}
    	// other if's to be added when new towers are added
    	
    	tile.setTower(tower);
    	return tower;
    }
    
    private static boolean inBounds(MatrixCoordinates mc)
    {
    	if (mc.getX() < 0 || mc.getY() < 0
    			|| mc.getX() > width || mc.getY() > height)
    	{
    		return false;
    	}
    	return true;
    }
    
    public static boolean verifyTowerPlacement(RealWorldCoordinates rwc) 
    {
    	// convert realWorldCoords to matrix
    	MatrixCoordinates mc = new MatrixCoordinates(rwc);
    	
    	// check if given mc is actually within the bounds of the matrix
    	if (!inBounds(mc))
    	{
    		return false;
    	}
    	
    	// check if this matrix position is legal
    	MatrixObject mo = map[mc.getY()][mc.getX()];
    	if (mo.getClass() == Path.class)
    	{
    		return false; // cannot place towers on path
    	}
    	else if ((mo.getClass() == Tile.class)
    			&& (((Tile) mo).getTower()) != null)
    	{
    		return false; // else it is a tile, but a tower exists here already
    	}
    	return true;
    }
}