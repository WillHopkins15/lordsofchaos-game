package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class MultiplayerButton extends MainMenuButton
{
    
    
    public MultiplayerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }
    
    @Override
    public void leftButtonAction() {
        selectSound.play(Game.getSoundEffectsVolume());
        Game.multiplayer = true;
        if (Game.setupClient()) {
            if (Game.getClient().isDefender()) {
                GameController.setPlayerType(true);
                Game.player = 0;
                targetScreen = Screen.DEFENDER_SCREEN;
            } else if (Game.getClient().isAttacker()) {
                GameController.setPlayerType(false);
                Game.player = 1;
                targetScreen = Screen.ATTACKER_SCREEN;
            }
            Game.currentScreen = targetScreen;
        } else {
            Game.multiplayer = false;
        }
    }
    
    @Override
    public void rightButtonAction() {
    
    }
}
