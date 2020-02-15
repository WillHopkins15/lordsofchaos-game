package org.lordsofchaos.gameobjects.towers;

import java.util.ArrayList;
import java.util.List;

import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.matrixobjects.Path;

public class Tower extends InteractiveObject
{
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
    }
    
    private Troop findNearestTroop()
    {
        if (inRange != null && !inRange.isEmpty())
        {
            // loop through inRange path objects to find closest troop
        }
        return null;
    }
    
    public void shoot()
    {
        target = findNearestTroop();
        GameController.shootTroop(this, target);
    }

}