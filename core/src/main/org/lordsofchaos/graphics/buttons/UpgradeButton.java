package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class UpgradeButton extends HoverButton
{
    
    public static boolean maxLevel;
    private static boolean doOnce = true;
    private Texture infoCardTexture;
    private Sprite infoCardSprite;
    public UpgradeButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);
        infoCardTexture = new Texture("UI/InfoCards/infoPanelTier" + (GameController.getDefenderUpgrade() + 1) +".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30,150);
    }
    
    @Override
    public void leftButtonAction() {
        if (maxLevel)
            return;
        if (!GameController.canDefenderUpgrade()){
            Game.playSound("ErrorSound");
            return;
        }

        selectSound.play(Game.getSoundEffectsVolume());
        Game.instance.setGhostTowerType(null); // this alerts Game that a tower isn't being placed, janky yes
        EventManager.defenderUpgrade();
        if(GameController.getDefenderUpgrade() != 3)
            updateTexture();
    }
    
    @Override
    public void rightButtonAction() {
    }
    public void updateTexture(){
        infoCardTexture.dispose();
        infoCardTexture = new Texture("UI/InfoCards/infoPanelTier" + (GameController.getDefenderUpgrade() + 1) +".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30,150);
    }
    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if(checkHover(x,y) && !maxLevel){
            infoCardSprite.draw(batch);
        }
        if(maxLevel && doOnce){
            doOnce = false;
            super.texture.dispose();
            super.texture = new Texture("UI/NewArtMaybe/defenderUpgradeButtonMAX.png");
            super.sprite = new Sprite(super.texture);
            super.sprite.setPosition(buttonX1,buttonY1);
        }
    }
}
