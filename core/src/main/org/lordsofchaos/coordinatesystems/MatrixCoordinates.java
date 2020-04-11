package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

public class MatrixCoordinates extends Coordinates
{
    
    public MatrixCoordinates(int x, int y) {
        setY(y);
        setX(x);
    }
    
    public MatrixCoordinates(RealWorldCoordinates rwc) {
        int sf = GameController.getScaleFactor();
        int y = (Math.round(rwc.getY() / sf));
        int x = (Math.round(rwc.getX() / sf));
        setY(clamp(y, 0, GameController.getLevel().getWidth() - 1));
        setX(clamp(x, 0, GameController.getLevel().getHeight() - 1));
    }
    
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    @Override
    public boolean equals(Coordinates other) {
        return super.equals(other);
    }
    
}
