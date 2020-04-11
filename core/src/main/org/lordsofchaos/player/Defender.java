package org.lordsofchaos.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.lordsofchaos.GameController;
import org.lordsofchaos.coordinatesystems.MatrixCoordinates;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public class Defender extends Player
{
    protected static Texture healthBarTexture;
    protected static Texture healthTexture;
    protected static Sprite healthBarSprite;
    protected static Sprite healthSprite;
    protected static BitmapFont hpCounter;
    protected int health;
    protected RealWorldCoordinates coordinates;
    
    public Defender(String Name) {
        super(Name);
        setCurrentMoney(100);
        setHealth(100);
        
        // change this when the actual coordinate for the game is decided
        MatrixCoordinates temp = new MatrixCoordinates(18, 18);
        int sf = GameController.getScaleFactor();
        int y = temp.getY() * sf;
        int x = temp.getX() * sf;
        RealWorldCoordinates coord = new RealWorldCoordinates(x + (sf / 2), y + (sf / 2));
        setCoordinates(coord);
        setMoneyBoost(50);
    }
    
    public void takeDamage(int damage) {
        int tmp = getHealth() - damage;
        if (tmp < 0)
            tmp = 0;
        setHealth(tmp);
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public RealWorldCoordinates getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(RealWorldCoordinates coordinates) {
        this.coordinates = coordinates;
    }
    
    public Sprite getHealthSprite() {
        return healthSprite;
    }
    
    public Sprite getHealthBarSprite() {
        return healthBarSprite;
    }
}