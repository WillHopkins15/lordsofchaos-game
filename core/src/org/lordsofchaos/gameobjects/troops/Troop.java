package org.lordsofchaos.gameobjects.troops;

import java.util.ArrayList;
import java.util.List;

import javax.security.sasl.RealmCallback;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.gameobjects.DamageType;
import org.lordsofchaos.gameobjects.InteractiveObject;
import org.lordsofchaos.matrixobjects.Path;
import org.lordsofchaos.player.Player;

public class Troop extends InteractiveObject
{
    protected float movementSpeed;
    protected int currentHealth;
    protected int maxHealth;
    protected DamageType armourType;
    protected List<java.nio.file.Path> path;
    
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
        MatrixCoordinates currentco = new MatrixCoordinates(realWorldCoordinates);
        int index = path.indexOf(currentco);
        if (index != (path.size()-1)) {
            MatrixCoordinates nexttile = new MatrixCoordinates();
            nexttile = path.get(index+1);
            String direction;

            if ((currentco.getY() - nexttile.getY()) == 0) {
                //x direction 
                if ((currentco.getX() - nexttile.getX()) == 1) {
                    direction = "west";
                } else {
                    direction = "east";
                }
            } else {
                if ((currentco.getY() - nexttile.getY()) == 1) {
                    direction = "north";
                } else {
                    direction = "south";
                }
            }

            switch (direction) {
                case "north":
                    realWorldCoordinates.setY(realWorldCoordinates.getY()-0.1);
                    break;
                case "east":
                    realWorldCoordinates.setX(realWorldCoordinates.getX()+0.1);
                    break;
                case "south":
                    realWorldCoordinates.setY(realWorldCoordinates.getY()+0.1);
                    break;
                case "west":
                    realWorldCoordinates.setX(realWorldCoordinates.getX()-0.1);
                    break;

            }

            MatrixCoordinates updatedco = new MatrixCoordinates(realWorldCoordinates);

            if ((currentco.equals(updatedco)) == false) {
                (path.get(index)).removeTroop(this);
                (path.get(index+1)).addTroop(this);
            }
           

        } else {
            (path.get(index)).removeTroop(this);
        }

    }
    
    public void attack(Player defender) 
    {
        if (getRealWorldCoordinates().compare(defender.getCoordinates())) {
            defender.setHealth(defender.getHealth() - damage);
        }
        
    }

}
