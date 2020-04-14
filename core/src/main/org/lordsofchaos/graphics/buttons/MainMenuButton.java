package org.lordsofchaos.graphics.buttons;

import org.lordsofchaos.Game;
import org.lordsofchaos.graphics.Screen;

public class MainMenuButton extends Button {

    protected Screen targetScreen;

    public MainMenuButton(String path, float buttonX1, float buttonY1, Screen screenLocation,
        Screen targetScreen) {
        super(path, buttonX1, buttonY1, screenLocation);
        this.targetScreen = targetScreen;
    }
    /**
     * When the button is left clicked if the game is not searching for a server it moves the game
     * to its targetScreen
     */
    public void leftButtonAction() {
        if (!Game.getSearchingForGame()) {
            selectSound.play(Game.getSoundEffectsVolume());
            Game.currentScreen = targetScreen;
        }

    }

    public void rightButtonAction() {
        return;
    }
}
