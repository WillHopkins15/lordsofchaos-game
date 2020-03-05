package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.gameobjects.towers.Tower;

public class Tile extends MatrixObject
{
    private Tower tower;
    private boolean isBuildable;
    
    public Tile(int y, int x, Tower tower, boolean isBuildable) {
        super(y, x);
        setTower(tower);
        setIsBuildable(isBuildable);
    }
    
    public Tower getTower() {
        return tower;
    }
    
    // Getters and setters
    public void setTower(Tower tower) {
        this.tower = tower;
    }
    
    public boolean getIsBuildable() {
        return isBuildable;
    }
    
    public void setIsBuildable(boolean isBuildable) {
        this.isBuildable = isBuildable;
    }
    
}
