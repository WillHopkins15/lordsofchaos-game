package org.lordsofchaos;

import java.util.ArrayList;
import java.util.List;

public class Tower extends InteractiveObject
{
    protected TowerType towerType;
    protected int range;
    protected DamageType damageType;
    
    protected Troop target;
    protected List<Path> inRange;
    
    public Tower(String spriteName, Coordinates coordinates,int cost, int damage, 
            TowerType towerType, int range, DamageType damageType)
    {
        super(spriteName, coordinates, cost, damage);
        setTowerType(towerType);
        setRange(range);
        setDamageType(damageType);
    }
    
    // Getters and Setters
    public void setTowerType(TowerType towerType)
    {
        this.towerType = towerType;
    }
    
    public TowerType getTowerType()
    {
        return towerType;
    }
    
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
    }

}
