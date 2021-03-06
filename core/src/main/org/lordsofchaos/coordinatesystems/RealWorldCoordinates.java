package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

public class RealWorldCoordinates extends Coordinates {

    public RealWorldCoordinates(int x, int y) {
        setY(y);
        setX(x);
    }

    public RealWorldCoordinates(MatrixCoordinates mc) {
        int sf = GameController.getScaleFactor();
        // int y = (GameController.getMap().length - mc.getY()) * sf;
        int y = mc.getY() * sf;
        int x = mc.getX() * sf;
        setY(y + (sf / 2)); // offset by 32
        setX(x + (sf / 2));
    }
}
