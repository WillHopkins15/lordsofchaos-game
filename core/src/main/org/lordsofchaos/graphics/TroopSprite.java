package org.lordsofchaos.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.lordsofchaos.gameobjects.troops.Troop;

public class TroopSprite {

    private float x;
    private float y;
    private Texture healthBarTextureBase;
    private Pixmap healthBarPixmapBase;
    private Sprite healthBarSpriteBase;
    private Texture healthBarTextureGreen;
    private Pixmap healthBarPixmapGreen;
    private Sprite healthBarSpriteGreen;
    private float healthPercentage;
    
    public TroopSprite(Troop unit) {
        x = Conversions.realWorldCoordinatesToScreenPosition(unit.getRealWorldCoordinates()).x;
        y = Conversions.realWorldCoordinatesToScreenPosition(unit.getRealWorldCoordinates()).y;
        healthPercentage = (float) unit.getCurrentHealth() / (float) unit.getMaxHealth();
        redHealthBar();
        greenHealthBar();
    }
    
    public void redHealthBar() {
        
        healthBarPixmapBase = new Pixmap(30, 5, Pixmap.Format.RGBA8888);
        healthBarPixmapBase.setColor(Color.RED);
        healthBarPixmapBase.fill();
        healthBarTextureBase = new Texture(healthBarPixmapBase);
        healthBarSpriteBase = new Sprite(healthBarTextureBase);
        healthBarSpriteBase.setPosition(x - 15, y + 20);
        
    }
    
    public void greenHealthBar() {
        
        healthBarPixmapGreen = new Pixmap((int) (30.0f * healthPercentage), 5, Pixmap.Format.RGBA8888);
        healthBarPixmapGreen.setColor(Color.GREEN);
        healthBarPixmapGreen.fill();
        healthBarTextureGreen = new Texture(healthBarPixmapGreen);
        healthBarSpriteGreen = new Sprite(healthBarTextureGreen);
        healthBarSpriteGreen.setPosition(x - 15, y + 20);
        
    }
    
    public void dispose() {
        healthBarPixmapBase.dispose();
        healthBarTextureBase.dispose();
        healthBarPixmapGreen.dispose();
        healthBarTextureGreen.dispose();
    }
    
    public Sprite getHealthBarSpriteBase() {
        return healthBarSpriteBase;
    }
    
    public Sprite getHealthBarSpriteGreen() {
        return healthBarSpriteGreen;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
}
