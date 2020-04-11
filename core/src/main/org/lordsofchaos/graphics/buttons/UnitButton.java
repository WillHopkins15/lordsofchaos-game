package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;
import org.lordsofchaos.graphics.buttons.Button;

public class UnitButton extends HoverButton
{
    private int unitPath;
    private int troopType;
    private Sprite infoCardSprite;
    private Texture infoCardTexture;
    public UnitButton(String path, float buttonX1, float buttonY1, Screen screenLocation, /*int unitPath,*/ int troopType) {
        super(path, buttonX1, buttonY1, screenLocation);
        //this.unitPath = unitPath;
        this.troopType = troopType;
        unitPath = Game.getCurrentPath();
        infoCardTexture = new Texture("UI/InfoCards/infoPanelUnit"+ (troopType + 1) + ".png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(30,150);
    }
    
    public void leftButtonAction() {
        if(GameController.canAffordTroop(troopType)) {
            unitPath = Game.getCurrentPath();
            selectSound.play(Game.getSoundEffectsVolume());
            EventManager.buildPlanChange(troopType, unitPath, 1, false);
        }
        else Game.playSound("ErrorSound");
    }
    
    public void rightButtonAction() {
        unitPath = Game.getCurrentPath();
        EventManager.buildPlanChange(troopType, unitPath, -1, false);
        selectSound.play(0.75f);
    }

    @Override
    public void update(int x, int y, SpriteBatch batch) {
        if(checkHover(x,y)){
            infoCardSprite.draw(batch);
        }
    }

    public int getUnitPath() {
        return unitPath;
    }
    
    public int getTroopType() {
        return troopType;
    }
}
