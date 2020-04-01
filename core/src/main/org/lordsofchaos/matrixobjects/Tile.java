package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.gameobjects.towers.Tower;

public class Tile extends MatrixObject
{
    private Tower tower;
    
    public Tile(int y, int x, Tower tower) {
        super(y, x);
        setTower(tower);
    }
    
    public Tower getTower() {
        return tower;
    }
    
    // Getters and setters
    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
