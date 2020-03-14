package org.lordsofchaos.graphics;

import org.lordsofchaos.EventManager;
import org.lordsofchaos.gameobjects.troops.Troop;
import org.lordsofchaos.gameobjects.troops.TroopType1;
import org.lordsofchaos.gameobjects.troops.TroopType2;
import org.lordsofchaos.gameobjects.troops.TroopType3;

public class UnitButton extends Button {
    private int unitPath;
    private int troopType;
    public UnitButton(String path, float buttonX1, float buttonY1,Screen screenLocation, int unitPath, int troopType) {
        super(path, buttonX1, buttonY1,screenLocation);
        this.unitPath = unitPath;
        this.troopType = troopType;
    }

    public void leftButtonAction() {
        selectSound.play(0.75f);
        EventManager.buildPlanChange(troopType, unitPath, 1, false);

    }
    public void rightButtonAction(){
        EventManager.buildPlanChange(troopType, unitPath, -1, false);
        selectSound.play(0.75f); }
    public int getUnitPath(){
        return unitPath;
    }
    public int getTroopType(){
        return troopType;
    }
}
