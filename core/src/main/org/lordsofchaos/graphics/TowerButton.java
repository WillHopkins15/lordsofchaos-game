package org.lordsofchaos.graphics;


import org.lordsofchaos.Game;
import org.lordsofchaos.gameobjects.TowerType;
import org.lordsofchaos.gameobjects.towers.Tower;
import org.lordsofchaos.gameobjects.towers.TowerType1;
import org.lordsofchaos.gameobjects.towers.TowerType2;
import org.lordsofchaos.gameobjects.towers.TowerType3;
import org.lordsofchaos.graphics.Button;

public class TowerButton extends Button {
    private TowerType towerType;
    public TowerButton(String path, float buttonX1, float buttonY1,Screen screenLocation, TowerType towerType) {
        super(path, buttonX1, buttonY1,screenLocation);
        this.towerType = towerType;
    }

    public void leftButtonAction() {
        System.out.println("Pressed TOWER BUTTON!!!!!");
       selectSound.play(0.75f);
       Game.instance.buildTrue();
       Game.instance.setGhostTowerType(towerType);
    }
    public void rightButtonAction(){
        return;
    }
}


