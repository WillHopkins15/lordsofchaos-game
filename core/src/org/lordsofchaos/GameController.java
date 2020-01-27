package org.lordsofchaos;

import java.util.List;

import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.matrixobjects.Tile;

public class GameController {

	private static int scaleFactor = 100;
    //Height and Width of the map
    private int height;
    private int width;
    @SuppressWarnings("unused")
	private int wave;

     //A list containing different lists that are have the co-ordinates of a paths
    private List<List<Path>> paths;

    //The 2 dimensional array to represent the map
    private MatrixObject[][] map;
    
    public static int getScaleFactor()
    {
    	return scaleFactor;
    }
    
    public void initialise()
    {
    	height = 10;
    	width = 10;
    	wave = 0;
        paths = MapGenerator.generatePaths();
        map = MapGenerator.generateMap(width, height, paths);
        debugVisualiseMap();
    }
    
    private void debugVisualiseMap()
    {
        for (int x = 0; x < width; x++)
        {
            System.out.println();
            for (int y = 0; y < height; y++)
            {
                if (map[y][x].getClass() == Tile.class)
                {
                    System.out.print("@");
                }
                else if (map[y][x].getClass() == Path.class)
                {
                    System.out.print("P");
                }
                else
                {
                    System.out.print("!");
                }
            }   
        }
    }
    
    public void shootTroop(Tower tower, Troop troop) {
        //will have to call sound and graphics for shooting at troop
        int temp;
        temp = troop.getCurrentHealth() - tower.getDamage();
        troop.setCurrentHealth(temp);

        if (troop.getCurrentHealth() <= 0) {
            //sound and graphic to remove the troop;
        }
    }

    //ask about the tower and matrix object issue;
    //cast as matrix object
    //don't need to pass co-ord

    /*public void objectPlaced(Tower tower){
        // anything that need to be done sound and graphics wise add later
        Map[coord.GetX()][coord.GetY()] = tower;

    }*/

    // Placement
    public void objectPlaced(Troop troop, RealWorldCoordinates rwc) {
    	MatrixCoordinates mc = new MatrixCoordinates(rwc);
    }
    
    public void objectPlaced(Tower tower, RealWorldCoordinates rwc) {
    	MatrixCoordinates mc = new MatrixCoordinates(rwc);
    }
    //

    // Removal
    public void objectRemoved(Troop troop, RealWorldCoordinates rwc) {
    	MatrixCoordinates mc = new MatrixCoordinates(rwc);
    }
    
    public void objectRemoved(Tower tower, RealWorldCoordinates rwc) {
    	MatrixCoordinates mc = new MatrixCoordinates(rwc);
    }
    //

    public void damageBase(Player player, Troop troop){
        int temp;
        temp  = player.getHealth() - troop.getDamage();

        if (temp <= 0) {
            player.setHealth(0);
            //end of game and relavant graphics and sound need to be done.
        } else {
            player.setHealth(temp);
        }

    }
    
    public void plusWave() {
        wave += 1;

    }



}