package org.lordsofchaos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

abstract public class Conversions {
    
    public static Vector2 realWorldCoordinatesToScreenPosition(RealWorldCoordinates rwc) {
        System.out.println(rwc.toString());
        Vector2 isometric = realWorldCooridinateToIsometric(rwc);
        System.out.println(isometric.toString());
        return new Vector2(isometric.x / 2, (Gdx.graphics.getHeight() + isometric.y) / 2);
    }
    
    public static Vector2 cartesianToIsometric(float x, float y) {
        return new Vector2(x - y, (x + y) * 0.5f);
    }
    
    public static Vector2 isometricToCartesian(float x, float y) {
        return new Vector2((2.0f * y + x) * 0.5f, (2.0f * y - x) * 0.5f);
    }
    
    public static RealWorldCoordinates isometricToRealWorldCoordinate(Vector2 vector) {
        Vector2 v2 = isometricToCartesian(vector.x, vector.y - 38);
        return new RealWorldCoordinates((int) (v2.x), (int) (v2.y + 1280));
    }
    
    public static Vector2 matrixCooridinateToIsometric(MatrixCoordinates mc) {
        final int sf = GameController.getScaleFactor();
        Vector2 v2 = cartesianToIsometric(mc.getX() * sf, mc.getY() * sf - 1280);
        v2.y += 38;
        return v2;
    }
    
    public static Vector2 realWorldCooridinateToIsometric(RealWorldCoordinates rwc) {
        Vector2 v2 = cartesianToIsometric(rwc.getX(), rwc.getY() - 1280);
        v2.y += 38;
        return v2;
    }
    
}
