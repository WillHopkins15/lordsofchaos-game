package org.lordsofchaos.coordinatesystems;

public abstract class Coordinates
{
    private int x;
    private int y;
    
    public int getX() {
        return x;
    }
    
    // Getters and Setters
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public boolean equals(Coordinates other) {
        if ((getX() == other.getX()) && (getY() == other.getY())) {
            return true;
        }
        return false;
    }
}
