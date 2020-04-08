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
        setCurrentMoney(1000);
        setHealth(100);
        //System.out.println("TEST1");
        /*
         * healthTexture = new Texture(Gdx.files.internal("UI/health.png"));
         * healthBarTexture = new Texture(Gdx.files.internal("UI/healthBar.png"));
         * healthBarSprite = new Sprite(healthBarTexture); healthSprite = new
         * Sprite(healthSprite); healthSprite.setScale(5); healthSprite.setPosition(225,
         * Gdx.graphics.getHeight() - 64); healthBarSprite.setScale(5);
         * healthBarSprite.setPosition(170, Gdx.graphics.getHeight() - 70);
         * hpCounter.getData().setScale(1.5f);
         */
        
        // change this when the actual coordinate for the game is decided
        MatrixCoordinates temp = new MatrixCoordinates(18, 18);
        //System.out.println(temp);
        int sf = GameController.getScaleFactor();
        
        // int y = (GameController.getMap().length - mc.getY()) * sf;
        int y = temp.getY() * sf;
        int x = temp.getX() * sf;
        RealWorldCoordinates coord = new RealWorldCoordinates(x + (sf / 2), y + (sf / 2));
        //System.out.println("defenders coords are :" + coord.getY() + "," + coord.getX());
        setCoordinates(coord);
        //System.out.println(getCoordinates());
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