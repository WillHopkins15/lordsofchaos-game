package org.lordsofchaos;

public abstract class GameObject
{
    protected String spriteName;
    protected Coordinates coordinates;
    
    public GameObject(String spriteName, Coordinates coordinates)
    {
        SetSpriteName(spriteName);
        SetCoordinates(coordinates);
    }

    // Getters and Setters
    public void SetSpriteName(String spriteName)
    {
        this.spriteName = spriteName;
    }
    
    public String GetSpriteName()
    {
        return spriteName;
    }
    
    public void SetCoordinates(Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }
    
    public Coordinates GetCoordinates()
    {
        return coordinates;
    }
    //
    
    public void Initialise()
    {
        
    }
    
    public void Remove()
    {
        
    }
}
