package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

public class MatrixCoordinates extends Coordinates
{
    public MatrixCoordinates(int y, int x) {
        setY(y);
        setX(x);
    }
    
    public MatrixCoordinates(RealWorldCoordinates rwc) {
        int sf = GameController.getScaleFactor();
        int y = rwc.getY() / sf;
        int x = rwc.getX() / sf;
        setY(y);
        setX(x);
    }
}
