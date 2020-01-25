package org.lordsofchaos;

public class Coordinates
{
    private int x;
    private int y;
    
    // Coordinates are always given y then x, so there in no confusion with the matrix
    public Coordinates (int y, int x)
    {
        setY(y);
        setX(x);
    }
    
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
