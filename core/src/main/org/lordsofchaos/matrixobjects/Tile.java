package org.lordsofchaos.matrixobjects;

import org.lordsofchaos.gameobjects.towers.Tower;

public class Tile extends MatrixObject
{
    private Tower tower;
    
    public Tile(int x, int y, Tower tower) {
        super(x, y);
        setTower(tower);
    }
    
    public Tower getTower() {
        return tower;
    }
    
    /**
     * If a tower is placed on this tile, set this.tower accordingly
     *
     * @param tower the tower that was placed
     */
    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
