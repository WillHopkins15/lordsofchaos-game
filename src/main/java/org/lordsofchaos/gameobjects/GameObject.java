package org.lordsofchaos.gameobjects;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public abstract class GameObject
{
    protected String spriteName;
    protected RealWorldCoordinates coordinates;
    
    public GameObject(String spriteName, RealWorldCoordinates rwc)
    {
        setSpriteName(spriteName);
        setCoordinates(rwc);
    }

    // Getters and Setters
    public void setSpriteName(String spriteName)
    {
        this.spriteName = spriteName;
    }
    
    public String getSpriteName()
    {
        return spriteName;
    }
    
    public void setCoordinates(RealWorldCoordinates rwc)
    {
        this.coordinates = rwc;
    }
    
    public RealWorldCoordinates getCoordinates()
    {
        return coordinates;
    }
    //
    
    public void initialise()
    {
        
    }
    
    public void remove()
    {
        
    }
}
