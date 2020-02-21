package org.lordsofchaos.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public class Defender extends Player {
    protected int Health;
    protected RealWorldCoordinates coordinates;
    protected static Texture healthBarTexture;
    protected static Texture healthTexture;
    protected static Sprite healthBarSprite;
    protected static Sprite healthSprite;
    protected static BitmapFont hpCounter;
    public Defender(String Name) {
        super(Name);
        setCurrentMoney(50);
        setHealth(100);
        System.out.println("TEST1");
        /*healthTexture = new Texture(Gdx.files.internal("UI/health.png"));
        healthBarTexture = new Texture(Gdx.files.internal("UI/healthBar.png"));
        healthBarSprite = new Sprite(healthBarTexture);
        healthSprite = new Sprite(healthSprite);
        healthSprite.setScale(5);
        healthSprite.setPosition(225, Gdx.graphics.getHeight() - 64);
        healthBarSprite.setScale(5);
        healthBarSprite.setPosition(170, Gdx.graphics.getHeight() - 70);
        hpCounter.getData().setScale(1.5f);*/

        //change this when the actual coordinate for the game is decided

        RealWorldCoordinates coord = new RealWorldCoordinates(8, 4);

        setCoordinates(coord);
        setMoneyBoost(50);

    }

    public void takeDamage(int damage)
    {
        int tmp = getHealth() - damage;
        if (tmp < 0)
            tmp = 0;
        setHealth(tmp);
    }

    public void setHealth(int Health) {
        this.Health = Health;
    }

    public int getHealth() {
        return Health;
    }
   
    public void setCoordinates(RealWorldCoordinates coordinates) {
        this.coordinates = coordinates;
    }
   
    public RealWorldCoordinates getCoordinates() {
        return coordinates;
    }

    public Sprite getHealthSprite(){
        return healthSprite;
    }
    public Sprite getHealthBarSprite(){
        return healthBarSprite;
    }
        
}