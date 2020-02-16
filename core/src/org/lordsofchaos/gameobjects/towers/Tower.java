package org.lordsofchaos.gameobjects.towers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.Coordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.matrixobjects.MatrixObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.MapGenerator;

public class Tower extends InteractiveObject
{
	protected float shootTimer = 0;
	protected float shootTimerLimit = 3;
    protected int range;
    protected DamageType damageType;
    protected Troop target;
    protected List<Path> inRange;
    
    public Tower(String spriteName, RealWorldCoordinates rwc, int cost, int damage, 
             int range, DamageType damageType)
    {
        super(spriteName, rwc, cost, damage);
        setRange(range);
        setDamageType(damageType);
    }
    
    // Getters and Setters
    public void setRange(int range)
    {
        this.range = range;
        findPathInRange();
    }
    
    public int getRange()
    {
        return range;
    }
    
    public void setDamageType(DamageType damageType)
    {
        this.damageType = damageType;
    }
    
    public DamageType getDamageType()
    {
        return damageType;
    }
    //
    
    private void findPathInRange()
    {
        // use range to find all in-range path objects in matrix
        inRange = new ArrayList<Path>();
        List<Coordinates> temp = new ArrayList<Coordinates>();
        MatrixCoordinates matrixco = new MatrixCoordinates(getRealWorldCoordinates());
        MatrixCoordinates tempco;

        int y = (matrixco.getY() - getRange());
        int ylimit = (y + 1 + (range * 2));

        int x = (matrixco.getX() - getRange());
        int xlimit = (x + 1 + (range * 2));

        for (int a = y; a < ylimit; a++) {
            for (int b = x; b <  xlimit; b++) {
                tempco = new MatrixCoordinates(a,b);
                temp.add(tempco);
            }
        }

        //will have to make edit to this when there are multiple paths;
        List<Coordinates> path = new ArrayList<Coordinates>();
        path = (MapGenerator.getPath1());
        Collections.reverse(path);

        for (int i = 0; i < path.size(); i++) {
            if ((temp.contains(path.get(i)))) {
                //need to add the path tile;
                GameController.getMatrixObject(path.get(i).getY(),path.get(i).getX());
            }

        }

    }
    
    private Troop findNearestTroop()
    {
        if (inRange != null && !inRange.isEmpty())
        {
            // loop through inRange path objects to find closest troop
            int count = 0;

            while (count < inRange.size()) {
                if ((inRange.get(count).getTroops()).isEmpty()) {
                    count++;
                } else {
                    int counter = 0;
                    boolean taken = true;
                    while ((taken) && (counter < (inRange.get(count).getTroops()).size())) {
                        if (!(inRange.get(count).getTroops()).get(counter).getTargeted()) {
                            taken = false;
                            return (inRange.get(count).getTroops()).get(counter);
                        } else {
                            counter++;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void resetTimer()
    {
    	shootTimer = 0;
    }
    
    public void shoot(float deltaTime)
    {
    	shootTimer += deltaTime;
    	if (shootTimer > shootTimerLimit)
    	{
    		target = findNearestTroop();
    		GameController.shootTroop(this, target);
    		resetTimer();
    	}
    }

}