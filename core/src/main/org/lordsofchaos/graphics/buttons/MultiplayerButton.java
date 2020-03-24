package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.GameController;
import org.lordsofchaos.graphics.Screen;

public class MultiplayerButton extends MenuButton {


    public MultiplayerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        if (Game.setupClient()) {
            Game.currentScreen = Screen.CHOOSE_FACTION;
            if (Game.getClient().isDefender()) {
                GameController.setPlayerType(true);
                Game.player = 0;
            } else if (Game.getClient().isAttacker()) {
                GameController.setPlayerType(false);
                Game.player = 1;
            }
            targetScreen = Screen.GAME;
            Game.currentScreen = targetScreen;
        }
    }

    @Override
    public void rightButtonAction() {

    }
}
