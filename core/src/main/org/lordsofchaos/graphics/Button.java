package org.lordsofchaos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

abstract public class Button
{
    protected Texture texture;
    protected Sprite sprite;
    protected float buttonX1;
    protected float buttonY1;
    protected float buttonX2;
    protected float buttonY2;
    protected boolean pressedStatus = false;
    protected Sound selectSound;
    protected Screen screenLocation;
    public Button(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound/click3.wav"));
        texture = new Texture(Gdx.files.internal(path));
        sprite = new Sprite(texture);
        sprite.setPosition(buttonX1, buttonY1);
        this.buttonX1 = buttonX1;
        this.buttonY1 = buttonY1;
        buttonX2 = sprite.getWidth() + buttonX1;
        buttonY2 = sprite.getHeight() + buttonY1;
        this.screenLocation = screenLocation;
    }
    
    public boolean checkClick(int x, int y) {
        if (x > buttonX1 && x < buttonX2)
            return y > buttonY1 && y < buttonY2;
        return false;
    }
    
    public Sprite getSprite() {
        return sprite;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public boolean getPressedStatus() {
        return pressedStatus;
    }
    
    public void setPressedStatus(boolean x) {
        pressedStatus = x;
    }
    
    public float getX() {
        return buttonX1;
    }
    
    public float getY() {
        return buttonY1;
    }

    public Screen getScreenLocation(){
        return screenLocation;
    }
    public void dispose() {
        texture.dispose();
        selectSound.dispose();
    }
    abstract public void leftButtonAction();
    abstract public void rightButtonAction();
}
