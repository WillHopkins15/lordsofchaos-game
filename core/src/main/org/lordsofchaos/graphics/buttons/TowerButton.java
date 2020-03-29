package org.lordsofchaos.graphics.buttons;


import org.lordsofchaos.Game;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.graphics.Screen;

public class TowerButton extends Button {
    private TowerType towerType;
    public TowerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, TowerType towerType) {
        super(path, buttonX1, buttonY1,screenLocation);
        this.towerType = towerType;
    }

    public void leftButtonAction() {
       selectSound.play(0.75f);
       Game.instance.buildTrue();
       Game.instance.setGhostTowerType(towerType);
    }

    public void rightButtonAction(){
        return;
    }
}


