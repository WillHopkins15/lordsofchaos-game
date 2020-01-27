package org.lordsofchaos.coordinatesystems;

public abstract class Coordinates
{
    private int x;
    private int y;
    
    // Getters and Setters
    public void setX(int x)
    {
        this.x = x;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    //
}
