package org.lordsofchaos.graphics;

import org.lordsofchaos.Game;

public class MultiplayerButton extends MenuButton {


    public MultiplayerButton(String path, float buttonX1, float buttonY1, Screen screenLocation, Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation, targetScreen);
    }

    @Override
    public void leftButtonAction() {
        selectSound.play(0.75f);
        Game.currentScreen = targetScreen;
        Game.multiplayer = true;
    }

    @Override
    public void rightButtonAction() {

    }
}
