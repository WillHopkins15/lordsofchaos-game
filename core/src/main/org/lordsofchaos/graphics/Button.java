package org.lordsofchaos.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Button
{
    protected Texture texture;
    protected Sprite sprite;
    protected float buttonX1;
    protected float buttonY1;
    protected float buttonX2;
    protected float buttonY2;
    protected boolean pressedStatus = false;
    
    public Button(String path, float buttonX1, float buttonY1) {
        
        texture = new Texture(Gdx.files.internal(path));
        sprite = new Sprite(texture);
        sprite.setPosition(buttonX1, buttonY1);
        this.buttonX1 = buttonX1;
        this.buttonY1 = buttonY1;
        buttonX2 = sprite.getWidth() + buttonX1;
        buttonY2 = sprite.getHeight() + buttonY1;
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
    
    public void dispose() {
        texture.dispose();
    }
}
