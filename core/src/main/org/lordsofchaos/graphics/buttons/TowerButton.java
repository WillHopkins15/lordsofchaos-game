package org.lordsofchaos.graphics.buttons;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.Game;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.graphics.Screen;

public class TowerButton extends HoverButton
{
    private TowerType towerType;
    private Sprite infoCardSprite;
    private Texture infoCardTexture;
    public TowerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, TowerType towerType) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.towerType = towerType;
        infoCardTexture = new Texture("UI/InfoCards/infoPanelTower" + towerType + ".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30,150);
    }
    
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.instance.buildTrue();
        Game.instance.setGhostTowerType(towerType);
    }
    
    public void rightButtonAction() {
        return;
    }

    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if(checkHover(x,y)){
            infoCardSprite.draw(batch);
        }
    }
}


