package org.lordsofchaos.matrixobjects;

public class Obstacle extends Tile {

    private ObstacleType type;

    public Obstacle(int x, int y, ObstacleType type) {
        super(x, y, null);
        this.type = type;
    }

    public ObstacleType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case RIVER:
                return "RIVER";
            case TREE:
                return "TREE";
            case ROCK:
                return "ROCK";
            default:
                return "BASE";
        }
    }
}
