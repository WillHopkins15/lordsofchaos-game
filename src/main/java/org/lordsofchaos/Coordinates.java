package org.lordsofchaos;

public class Coordinates
{
    private int x;
    private int y;
    
    // Coordinates are always given y then x, so there in no confusion with the matrix
    public Coordinates (int y, int x)
    {
        SetY(y);
        SetX(x);
    }
    
    // Getters and Setters
    public void SetX(int x)
    {
        this.x = x;
    }
    
    public void SetY(int y)
    {
        this.y = y;
    }
    
    public int GetX()
    {
        return x;
    }
    
    public int GetY()
    {
        return y;
    }
    //
}
