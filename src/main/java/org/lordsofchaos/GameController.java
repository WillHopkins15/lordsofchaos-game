package org.lordsofchaos;

import java.util.List;

public class GameController {

    //Height and Width of the map
    int Height;
    int Width;
    int Wave;

     //A list containing different lists that are have the co-ordinates of a paths
    List<List<Coordinates>> Paths;

    //The 2 dimensional array to represent the map
    MatrixObject[][] Map;

    //can't finish this method at this time because we need more info from graphics
    public MatrixObject[][] generateMap() {

    }

     public void ShootTroop(Tower tower, Troop troop) {
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

    public void ObjectPlaced(Tower tower){
        // anything that need to be done sound and graphics wise add later
        Map[coord.GetX()][coord.GetY()] = tower;

    }

    public void ObjectPlaced(Troop troop) {
        //talk to tim  about adding an add troop function to the path class 
    }

    public void ObjectRemoved(Troop troop, Coordinates coord) {
        //talk to tim about adding remove troop function to the path class
    }

    public void DamageBase(Troop troop){
        // need to make a new class called base 
    }
    
    public void plusWave() {
        Wave = Wave + 1;

    }



}