package org.lordsofchaos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

abstract public class Convertions
{
    public static Vector2 realWorldCoordinatesToScreenPosition(RealWorldCoordinates rwc) {
        Vector2 screenPosition = new Vector2();
        Vector2 isometric = realWorldCooridinateToIsometric(rwc);
        screenPosition.x = isometric.x / 2;
        screenPosition.y = Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() - isometric.y) / 2;
        return screenPosition;
    }
    
    public static Vector2 cartesianToIsometric(float x, float y) {
        Vector2 isometric = new Vector2();
        isometric.x = x - y;
        isometric.y = (x + y) * 0.5f;
        return isometric;
    }
    
    public static Vector2 isometricToCartesian(float x, float y) {
        Vector2 cartesian = new Vector2();
        cartesian.x = (2.0f * y + x) * 0.5f;
        cartesian.y = (2.0f * y - x) * 0.5f;
        return cartesian;
    }
    
    public static RealWorldCoordinates isometricToRealWorldCoordinate(Vector2 vector) {
        
        Vector2 diff = cartesianToIsometric(1280, 1280);
        Vector2 v2 = isometricToCartesian(vector.x, vector.y - 38);
        
        int x = (int) (v2.x + diff.x);
        int y = (int) (v2.y + diff.y);
        
        return new RealWorldCoordinates(y, x);
    }
    
    public static Vector2 realWorldCooridinateToIsometric(RealWorldCoordinates rwc) {
        Vector2 diff = cartesianToIsometric(1280, 1280);
        float x = rwc.getX() - diff.x;
        float y = rwc.getY() - diff.y;
        Vector2 v2 = cartesianToIsometric(x, y);
        v2.y += 38;
        return v2;
    }
    
}
