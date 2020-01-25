package org.lordsofchaos;

public abstract class GameObject
{
    protected String spriteName;
    protected Coordinates coordinates;
    
    public GameObject(String spriteName, Coordinates coordinates)
    {
        setSpriteName(spriteName);
        setCoordinates(coordinates);
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
    
    public void setCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }
    
    public Coordinates getCoordinates()
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
