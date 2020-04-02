package org.lordsofchaos.matrixobjects;

public class Obstacle extends Tile {
    
    private ObstacleType type;
    
    public Obstacle(int y, int x, ObstacleType type) {
        super(y, x, null);
        this.type = type;
    }
    
    public ObstacleType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch (type) {
            case RIVER: return "RIVER";
            case TREE: return "TREE";
            case ROCK: return "ROCK";
            default: return "BASE";
        }
    }
}
