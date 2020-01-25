package org.lordsofchaos.gameobjects.troops;

import java.util.ArrayList;
import java.util.List;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.matrixobjects.Path;

public class Troop extends InteractiveObject
{
    protected float movementSpeed;
    protected int currentHealth;
    protected int maxHealth;
    protected DamageType armourType;
    protected List<Path> path;
    
    public Troop(String spriteName, int cost, int damage,
            float movementSpeed, int maxHealth, DamageType armourType, List<Path> path)
    {
        super(spriteName, new RealWorldCoordinates(path.get(0).getMatrixPosition())
        		, cost, damage);
        setMovementSpeed(movementSpeed);
        setCurrentHealth(maxHealth);
        setMaxHealth(maxHealth);
        setPath(path);
    }
    
    // Getters and setters
    public void setMovementSpeed(float movementSpeed)
    {
        this.movementSpeed = movementSpeed;
    }
    
    public float getMovementSpeed()
    {
        return movementSpeed;
    }
    
    public void setMaxHealth(int health)
    {
        maxHealth = health;
    }
    
    public int getMaxHealth()
    {
        return maxHealth;
    }
    
    public void setCurrentHealth(int health)
    {
        currentHealth = health;
    }
    
    public int getCurrentHealth()
    {
        return currentHealth;
    }
    
    public void setPath(List<Path> path)
    {
        this.path = path;
    }
    
    public List<Path> getPath()
    {
        if (path == null)
        {
            path = new ArrayList<Path>();
        }
        return path;
    }
    //
    
    public void move()
    {
        // move along set path
    }
    
    public void attack()
    {
        
    }

}
