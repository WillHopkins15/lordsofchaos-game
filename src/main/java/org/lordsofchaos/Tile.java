package org.lordsofchaos;

public class Tile extends MatrixObject
{
    private Tower tower;
    
    public Tile(int y, int x, Tower tower)
    {
        super(y, x);
        SetTower(tower);
    }
    
    // Getters and setters
    public void SetTower(Tower tower)
    {
        this.tower = tower;
    }
    
    public Tower GetTower()
    {
        return tower;
    }
    //
}
