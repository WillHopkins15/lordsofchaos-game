package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.graphics.Screen;

public class HoverUI extends HoverButton
{
    private Sprite infoCardSprite;
    private Texture infoCardTexture;
    
    public HoverUI(String path, float buttonX1, float buttonY1, Screen screenLocation, String infoCardPath, int infoCardX, int infoCardY) {
        super(path, buttonX1, buttonY1, screenLocation);
        infoCardTexture = new Texture(Gdx.files.internal(infoCardPath));
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(infoCardX, infoCardY);
        
        System.out.println("X1: " + super.buttonX1 + " X2: " + super.buttonX2 + " Y1: " + super.buttonY1 + " Y2: " + super.buttonY2);
    }
    
    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if (checkHover(x, y)) {
            infoCardSprite.draw(batch);
        }
    }
    
    @Override
    public void leftButtonAction() {
        return;
    }
}
