package org.lordsofchaos.graphics.buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
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
        infoCardTexture = new Texture("UI/NewArtMaybe/panel.png");
        infoCardSprite = new Sprite(infoCardTexture);
        infoCardSprite.setPosition(10,150);
    }
    
    public void leftButtonAction() {
        System.out.println(Game.getCurrentPath() + "!!!!!!!!!");
        unitPath = Game.getCurrentPath();
        System.out.println(unitPath + ", " + troopType);
        selectSound.play(0.75f);
        EventManager.buildPlanChange(troopType, unitPath, 1, false);
        
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
