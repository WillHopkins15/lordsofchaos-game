package org.lordsofchaos.matrixobjects;

public class Obstacle extends Tile {

    private ObstacleType type;

    public Obstacle(int y, int x, ObstacleType type) {
        super(y, x, null);
        setIsBuildable(false);
        this.type = type;
    }

    public ObstacleType getType() {
        return type;
    }
}
