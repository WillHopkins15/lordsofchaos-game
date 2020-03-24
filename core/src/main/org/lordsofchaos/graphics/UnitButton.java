package org.lordsofchaos.graphics;

import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.buttons.Button;

public class UnitButton extends Button {
    private int unitPath;
    private int troopType;
    public UnitButton(String path, float buttonX1, float buttonY1,Screen screenLocation, /*int unitPath,*/ int troopType) {
        super(path, buttonX1, buttonY1,screenLocation);
        //this.unitPath = unitPath;
        this.troopType = troopType;
        unitPath = Game.getCurrentPath();
    }

    public void leftButtonAction() {
        System.out.println(Game.getCurrentPath() + "!!!!!!!!!");
        unitPath = Game.getCurrentPath();
        System.out.println(unitPath + ", " + troopType);
        selectSound.play(0.75f);
        EventManager.buildPlanChange(troopType,unitPath, 1, false);

    }
    public void rightButtonAction(){
        unitPath = Game.getCurrentPath();
        EventManager.buildPlanChange(troopType, unitPath, -1, false);
        selectSound.play(0.75f);
    }
    public int getUnitPath(){
        return unitPath;
    }
    public int getTroopType(){
        return troopType;
    }
}
