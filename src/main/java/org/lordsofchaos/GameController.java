package org.lordsofchaos;

import java.util.List;

public class GameController {

    //Height and Width of the map, using 10 for debug values
    int Height = 10;
    int Width = 10;
    int Wave;

     //A list containing different lists that are have the co-ordinates of a paths
    List<List<Path>> Paths;

    //The 2 dimensional array to represent the map
    MatrixObject[][] Map;
    
    public void Initialise()
    {
        Paths = MapGenerator.generatePaths();
        Map= MapGenerator.generateMap(Width, Height, Paths);
        debugVisualiseMap();
    }
    
    private void debugVisualiseMap()
    {
        for (int x = 0; x < Width; x++)
        {
            System.out.println();
            for (int y = 0; y < Height; y++)
            {
                if (Map[y][x].getClass() == Tile.class)
                {
                    System.out.print("@");
                }
                else if (Map[y][x].getClass() == Path.class)
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
        temp = troop.GetCurrentHealth() - tower.GetDamage();
        troop.SetCurrentHealth(temp);

        if (troop.currentHealth <= 0) {
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
    public void objectPlaced(Troop troop, int matrixX, int matrixY) {
    }
    
    public void objectPlaced(Tower tower, int matrixX, int matrixY) {
    }
    //

    // Removal
    public void objectRemoved(Troop troop, int matrixX, int matrixY) {
    }
    
    public void objectRemoved(Tower tower, int matrixX, int matrixY) {
    }
    //

    public void damageBase(Player player, Troop troop){
        int temp;
        temp  = player.getHealth() - troop.GetDamage();

        if (temp <= 0) {
            player.setHealth(0);
            //end of game and relavant graphics and sound need to be done.
        } else {
            player.setHealth(temp);
        }

    }
    
    public void plusWave() {
        Wave = Wave + 1;

    }



}