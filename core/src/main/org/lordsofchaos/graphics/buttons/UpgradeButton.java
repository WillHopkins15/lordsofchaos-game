package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.EventManager;
import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class UpgradeButton extends Button
{
    
    public static boolean maxLevel;
    
    public UpgradeButton(String path, float buttonX1, float buttonY1, Screen screenLocation) {
        super(path, buttonX1, buttonY1, screenLocation);
    }
    
    @Override
    public void leftButtonAction() {
        if (maxLevel)
            return;
        selectSound.play(Game.getSoundEffectsVolume());
        Game.instance.setGhostTowerType(null); // this alerts Game that a tower isn't being placed, janky yes
        EventManager.defenderUpgrade();
    }
    
    @Override
    public void rightButtonAction() {
    }
}
