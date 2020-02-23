package org.lordsofchaos.coordinatesystems;

import org.lordsofchaos.GameController;

import java.io.Serializable;

public class RealWorldCoordinates extends Coordinates implements Serializable
{
    public RealWorldCoordinates(int y, int x) {
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
